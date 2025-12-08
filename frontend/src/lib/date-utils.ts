import { format, isValid, parseISO } from "date-fns";

export const formatDate = (
  date: Date | string | null | undefined,
  formatStr = "dd.MM.yyyy"
): string => {
  if (!date) return "";

  try {
    const dateObj = typeof date === "string" ? parseISO(date) : date;

    if (!(dateObj instanceof Date) || !isValid(dateObj)) {
      return "";
    }

    return format(dateObj, formatStr);
  } catch {
    return "";
  }
};
