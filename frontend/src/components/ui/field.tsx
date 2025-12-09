import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import type { FieldError as FieldErrorType } from "react-hook-form";

import { cn } from "@/lib/utils";
import { useLanguage } from "@/contexts/LanguageContext";

const fieldVariants = cva("group/field flex", {
  variants: {
    orientation: {
      horizontal: "flex-row items-center justify-between gap-4",
      vertical: "flex-col items-start gap-1.5",
      responsive:
        "flex-col items-start gap-1.5 sm:flex-row sm:items-center sm:justify-between sm:gap-4",
    },
  },
  defaultVariants: {
    orientation: "vertical",
  },
});

export interface FieldProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof fieldVariants> {}

const Field = React.forwardRef<HTMLDivElement, FieldProps>(
  ({ className, orientation, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={cn(fieldVariants({ orientation, className }))}
        {...props}
      />
    );
  }
);
Field.displayName = "Field";

const FieldContent = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div
    ref={ref}
    className={cn("flex flex-1 flex-col gap-1.5", className)}
    {...props}
  />
));
FieldContent.displayName = "FieldContent";

const FieldLabel = React.forwardRef<
  HTMLLabelElement,
  React.LabelHTMLAttributes<HTMLLabelElement>
>(({ className, ...props }, ref) => (
  <label
    ref={ref}
    className={cn(
      "text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70",
      "group-data-[invalid=true]/field:text-destructive",
      className
    )}
    {...props}
  />
));
FieldLabel.displayName = "FieldLabel";

const FieldDescription = React.forwardRef<
  HTMLParagraphElement,
  React.HTMLAttributes<HTMLParagraphElement>
>(({ className, ...props }, ref) => (
  <p
    ref={ref}
    className={cn("text-[0.8rem] text-muted-foreground", className)}
    {...props}
  />
));
FieldDescription.displayName = "FieldDescription";

interface FieldErrorProps extends React.HTMLAttributes<HTMLParagraphElement> {
  errors?: (FieldErrorType | undefined)[];
}

const FieldError = React.forwardRef<HTMLParagraphElement, FieldErrorProps>(
  ({ className, errors, ...props }, ref) => {
    const { t } = useLanguage();
    if (!errors || errors.length === 0) return null;

    const error = errors.find((e) => e !== undefined);
    if (!error) return null;

    return (
      <p
        ref={ref}
        className={cn("text-[0.8rem] font-medium text-destructive", className)}
        {...props}
      >
        {t(error.message as string)}
      </p>
    );
  }
);
FieldError.displayName = "FieldError";

const FieldGroup = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div
    ref={ref}
    className={cn(
      "flex flex-col gap-4",
      "[&[data-slot='checkbox-group']]:gap-2",
      className
    )}
    {...props}
  />
));
FieldGroup.displayName = "FieldGroup";

const fieldLegendVariants = cva("font-medium", {
  variants: {
    variant: {
      default: "text-base",
      label: "text-sm",
    },
  },
  defaultVariants: {
    variant: "default",
  },
});

interface FieldLegendProps
  extends React.HTMLAttributes<HTMLLegendElement>,
    VariantProps<typeof fieldLegendVariants> {}

const FieldLegend = React.forwardRef<HTMLLegendElement, FieldLegendProps>(
  ({ className, variant, ...props }, ref) => (
    <legend
      ref={ref}
      className={cn(
        fieldLegendVariants({ variant }),
        "group-data-[invalid=true]:text-destructive",
        className
      )}
      {...props}
    />
  )
);
FieldLegend.displayName = "FieldLegend";

const FieldSet = React.forwardRef<
  HTMLFieldSetElement,
  React.FieldsetHTMLAttributes<HTMLFieldSetElement>
>(({ className, ...props }, ref) => (
  <fieldset
    ref={ref}
    className={cn("flex flex-col gap-2 group", className)}
    {...props}
  />
));
FieldSet.displayName = "FieldSet";

const FieldSeparator = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn("h-px bg-border", className)} {...props} />
));
FieldSeparator.displayName = "FieldSeparator";

const FieldTitle = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div
    ref={ref}
    className={cn("text-sm font-medium leading-none", className)}
    {...props}
  />
));
FieldTitle.displayName = "FieldTitle";

export {
  Field,
  FieldContent,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSeparator,
  FieldSet,
  FieldTitle,
};
