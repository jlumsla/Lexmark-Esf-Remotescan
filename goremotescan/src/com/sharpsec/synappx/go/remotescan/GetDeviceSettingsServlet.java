package com.sharpsec.synappx.go.remotescan;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lexmark.prtapp.webadmin.ApplicationServlet;

public class GetDeviceSettingsServlet extends HttpServlet
{
   // Eclipse gives a warning if this is not there
   private static final long serialVersionUID = 1L;

   public GetDeviceSettingsServlet()
   {
   }

   /**
    * Get the proper locale.  For framework 2.0 and above, this is set
    * by the language bar provided by the printer's web server, and is
    * accessible via ApplicationServlet.getLocale(request).  For
    * framework 1.2 and previous, Locale.getDefault() should be used.
    */
   Locale getLocaleHelper(HttpServletRequest request)
   {
      Locale locale = null;

      try
      {
         // To support both frameworks 1.2 and 2.0, we can't assume this new
         // function always exists.  So if it throws a NoClassDefFoundError or
         // similar, get the locale the old way
         locale = ApplicationServlet.getLocale(request);
      }
      catch (Throwable e)
      {
         Activator.getLog()
               .debug("Using default locale - must be on old framework");
         locale = Locale.getDefault();
      }
      Activator.getLog()
            .debug("Locale to use for translated resources is " + locale);
      return locale;
   }

   /**
    * Get the dynamic content.  A servlet in OSGi behaves much like any
    * other Java servlet.
    */
   protected void doGet(HttpServletRequest request,
         HttpServletResponse response) throws ServletException, IOException
   {
      response.setContentType("text/xml;charset=utf-8");
      System.out.println("Executing GetDeviceSettingsServlet");
      
      // Note: writes to the response's PrintWriter go to the web page, whereas
      // writes to System.out() go to the OSGi log (as with any other bundle).
      PrintWriter out = response.getWriter();

      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      out.append("<result xmlns=\"urn:schemas-sc-jp:mfp:osa-1-1\" product-family=\"133\" product-version=\"x034Z20d>");
      out.append("<GetDeviceSettingsResponse/>");
      out.append("</result>");  
   }

}
