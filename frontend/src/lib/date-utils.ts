export const formatDate = (date: Date | string, formatStr: string = "MMM dd, yyyy"): string => {
  const d = typeof date === "string" ? new Date(date) : date;

  const months = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  ];

  const day = d.getDate();
  const month = months[d.getMonth()];
  const year = d.getFullYear();

  if (formatStr === "MMM dd, yyyy") {
    return `${month} ${day.toString().padStart(2, '0')}, ${year}`;
  }

  if (formatStr === "yyyy-MM-dd") {
    const monthNum = (d.getMonth() + 1).toString().padStart(2, '0');
    const dayNum = day.toString().padStart(2, '0');
    return `${year}-${monthNum}-${dayNum}`;
  }

  return d.toLocaleDateString();
};
