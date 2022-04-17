export function bearer() {
  // return "Bearer ".concat(sessionStorage.getItem("token"));
  return sessionStorage.getItem("token");
}
