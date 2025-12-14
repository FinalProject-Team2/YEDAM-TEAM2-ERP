package store.yd2team.common.util;

public class UrlUtil {
  private UrlUtil() {}

  public static String normalize(String url) {
    if (url == null) return null;
    String u = url.trim();
    if (!u.startsWith("/")) u = "/" + u;
    if (u.length() > 1 && u.endsWith("/")) u = u.substring(0, u.length() - 1);
    return u;
  }
}
