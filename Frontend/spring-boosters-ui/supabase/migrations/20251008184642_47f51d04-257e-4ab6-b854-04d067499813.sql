-- Create profiles table
CREATE TABLE public.profiles (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  full_name TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Enable RLS on profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Profiles policies
CREATE POLICY "Users can view their own profile"
  ON public.profiles FOR SELECT
  USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile"
  ON public.profiles FOR UPDATE
  USING (auth.uid() = id);

CREATE POLICY "Users can insert their own profile"
  ON public.profiles FOR INSERT
  WITH CHECK (auth.uid() = id);

-- Create vaccination_types table (master data)
CREATE TABLE public.vaccination_types (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL UNIQUE,
  description TEXT,
  recommended_interval_months INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Enable RLS on vaccination_types (read-only for all authenticated users)
ALTER TABLE public.vaccination_types ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view vaccination types"
  ON public.vaccination_types FOR SELECT
  USING (true);

-- Create vaccinations table
CREATE TABLE public.vaccinations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
  vaccination_type_id UUID NOT NULL REFERENCES public.vaccination_types(id),
  vaccination_date DATE NOT NULL,
  next_due_date DATE,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Enable RLS on vaccinations
ALTER TABLE public.vaccinations ENABLE ROW LEVEL SECURITY;

-- Vaccinations policies
CREATE POLICY "Users can view their own vaccinations"
  ON public.vaccinations FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own vaccinations"
  ON public.vaccinations FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own vaccinations"
  ON public.vaccinations FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own vaccinations"
  ON public.vaccinations FOR DELETE
  USING (auth.uid() = user_id);

-- Function to automatically create profile on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'full_name', '')
  );
  RETURN NEW;
END;
$$;

-- Trigger to create profile on user signup
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW
  EXECUTE FUNCTION public.handle_new_user();

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION public.update_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$;

-- Triggers for updated_at
CREATE TRIGGER update_profiles_updated_at
  BEFORE UPDATE ON public.profiles
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at();

CREATE TRIGGER update_vaccinations_updated_at
  BEFORE UPDATE ON public.vaccinations
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at();

-- Insert some common vaccination types
INSERT INTO public.vaccination_types (name, description, recommended_interval_months) VALUES
  ('COVID-19', 'COVID-19 vaccination', 6),
  ('Influenza (Flu)', 'Annual flu vaccine', 12),
  ('Tetanus/Diphtheria', 'Tetanus and diphtheria booster', 120),
  ('Measles/Mumps/Rubella (MMR)', 'MMR vaccine', 0),
  ('Hepatitis A', 'Hepatitis A vaccine', 0),
  ('Hepatitis B', 'Hepatitis B vaccine', 0),
  ('Pneumococcal', 'Pneumococcal vaccine', 60),
  ('Shingles (Herpes Zoster)', 'Shingles vaccine', 0),
  ('Human Papillomavirus (HPV)', 'HPV vaccine', 0),
  ('Varicella (Chickenpox)', 'Chickenpox vaccine', 0);