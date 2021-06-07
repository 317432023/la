package com.jeetx.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CookieUtils
{
  public static String getCookieValue(HttpServletRequest request, String cookieName)
  {
    return getCookieValue(request, cookieName, false);
  }

  public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder)
  {
    Cookie[] cookieList = request.getCookies();
    if ((cookieList == null) || (cookieName == null)) {
      return null;
    }
    String retValue = null;
    try {
      for (int i = 0; i < cookieList.length; i++)
        if (cookieList[i].getName().equals(cookieName)) {
          if (isDecoder) {
            retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8"); break;
          }
          retValue = cookieList[i].getValue();

          break;
        }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return retValue;
  }

  public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString)
  {
    Cookie[] cookieList = request.getCookies();
    if ((cookieList == null) || (cookieName == null)) {
      return null;
    }
    String retValue = null;
    try {
      for (int i = 0; i < cookieList.length; i++)
        if (cookieList[i].getName().equals(cookieName)) {
          retValue = URLDecoder.decode(cookieList[i].getValue(), encodeString);
          break;
        }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return retValue;
  }

  public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue)
  {
    setCookie(request, response, cookieName, cookieValue, -1);
  }

  public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage)
  {
    setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
  }

  public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, boolean isEncode)
  {
    setCookie(request, response, cookieName, cookieValue, -1, isEncode);
  }

  public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, boolean isEncode)
  {
    doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, isEncode);
  }

  public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, String encodeString)
  {
    doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, encodeString);
  }

  public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)
  {
    doSetCookie(request, response, cookieName, "", -1, false);
  }

  private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, boolean isEncode)
  {
    try
    {
      if (cookieValue == null)
        cookieValue = "";
      else if (isEncode) {
        cookieValue = URLEncoder.encode(cookieValue, "utf-8");
      }
      Cookie cookie = new Cookie(cookieName, cookieValue);
      if (cookieMaxage > 0)
        cookie.setMaxAge(cookieMaxage);
      if (null != request) {
        String domainName = getDomainName(request);

        cookie.setDomain(domainName);
      }

      cookie.setPath("/");
      response.addCookie(cookie);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, String encodeString)
  {
    try
    {
      if (cookieValue == null)
        cookieValue = "";
      else {
        cookieValue = URLEncoder.encode(cookieValue, encodeString);
      }
      Cookie cookie = new Cookie(cookieName, cookieValue);
      if (cookieMaxage > 0)
        cookie.setMaxAge(cookieMaxage);
      if (null != request) {
        String domainName = getDomainName(request);
        if (!"localhost".equals(domainName)) {
          cookie.setDomain(domainName);
        }
      }
      cookie.setPath("/");
      response.addCookie(cookie);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static final String getDomainName(HttpServletRequest request)
  {
    String serverName = request.getRequestURL().toString();

    return getDomainName(serverName);
  }

  private static final String getDomainName(String serverName)
  {
    if ((serverName == null) || (serverName.equals(""))) {
      return "";
    }

    serverName = serverName.toLowerCase();

    if (serverName.startsWith("http://")) serverName = serverName.substring(7);
    else if (serverName.startsWith("https://")) serverName = serverName.substring(8);

    int end = serverName.indexOf("/");
    if (end != -1) serverName = serverName.substring(0, end);

    int idx = serverName.indexOf(":");
    if (idx != -1) serverName = serverName.substring(0, idx);

    if ((Pattern.matches("^((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))$", serverName)) || ("localhost".equals(serverName)))
    {
      return serverName;
    }

    if (serverName.startsWith("www.")) {
      return serverName.substring(3);
    }

    if (serverName.split("\\.").length >= 3) {
      return serverName.substring(serverName.indexOf("."));
    }

    return "." + serverName;
  }

  public static void main(String[] args) {
    String url = "http://freecapital.hk:8080/index.html";
    String dm = getDomainName(url);
    System.out.println(dm);
  }
}