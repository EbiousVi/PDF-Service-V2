const port = ":6060";
const separator = "//";

export function getUrl(path) {
    return location.protocol + separator + location.hostname + port + path;
  }
  