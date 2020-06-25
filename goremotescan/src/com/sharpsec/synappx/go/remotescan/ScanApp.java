
package com.sharpsec.synappx.go.remotescan;

import java.util.Locale;

import org.osgi.service.http.HttpService;
import org.ungoverned.gravity.servicebinder.Lifecycle;
import org.ungoverned.gravity.servicebinder.ServiceBinderContext;

import com.lexmark.prtapp.util.Messages;
import com.lexmark.prtapp.util.Web;
//import com.lexmark.prtapp.webadmin.Consts;
import com.lexmark.prtapp.webapp.WebApp;

public class ScanApp implements Lifecycle, WebApp
{
   // NOTE: To require authentication to a web app, instead of using
   // Consts.WEB_APPS_PATH, use Consts.WEB_APP_AUTH_PATH as the "root".
   
   /** Alias to the web servlets. */
//   public static final String REQUESTUISESSIONID_ALIAS_INTERNAL = Consts.WEB_APPS_PATH + "/mfpcommon/RequestUISessionId/v1";
//   public static final String RELEASEUISESSIONID_ALIAS_INTERNAL = Consts.WEB_APPS_PATH + "/mfpcommon/ReleaseUISessionId/v1";
   public static final String REQUESTUISESSIONID_ALIAS_INTERNAL = "/mfpcommon/RequestUISessionId/v1";
   public static final String RELEASEUISESSIONID_ALIAS_INTERNAL = "/mfpcommon/ReleaseUISessionId/v1";
   public static final String SETDEVICECONTEXT_ALIAS_INTERNAL = "/mfpcommon/SetDeviceContext/v1";
   public static final String GETDEVICESETTINGS_ALIAS_INTERNAL = "/mfpcommon/GetDeviceSettings/v1";
   public static final String EXECUTEJOB_ALIAS_INTERNAL = "/mfpscan/ExecuteJob/v1";
   public static final String GETJOBSETTABLEELEMENTS_ALIAS_INTERNAL = "/mfpscan/GetJobSettableElements/v1";
   public static final String GETJOBSTATUS_ALIAS_INTERNAL = "/mfpscan/GetJobStatus/v1";
   
   // Used to register web content with the web server provided by OSGi
   private HttpService _httpService = null;

   // ServiceBinder context - retain in case it is needed
   private ServiceBinderContext _sbc = null;

   // Constructor called by Service Binder.
   public ScanApp(ServiceBinderContext sbc)
   {
      _sbc = sbc;
   }

   public String getId()
   {
      return "ScanApp";
   }

   public String getName(Locale locale)
   {
      Messages messages = new Messages("Resources", locale,
            getClass().getClassLoader());
      return messages.getString("webapp.name");
   }

   public String getDescription(Locale locale)
   {
      Messages messages = new Messages("Resources", locale,
            getClass().getClassLoader());
      return messages.getString("application.web.description");
   }

   /**
    * This should return the URL that this app wants to publish to the world.
    * The URL can refer to static content or a servlet, but it must be something
    * that has been registered with the HttpService or else it won't work...
    */
   public String getUrl()
   {
      System.out.format("url: %s", REQUESTUISESSIONID_ALIAS_INTERNAL);
      return Web.getRootPath() + REQUESTUISESSIONID_ALIAS_INTERNAL;
   }

   public String getIconURL()
   {
      return null;
   }

   /**
    * ServiceBinder calls this when we receive an HttpService.  See
    * Metadata.xml
    */
   public void addHttpService(HttpService svc)
   {
      _httpService = svc;
   }

   /**
    * ServiceBinder calls this when the HttpService goes away.
    */
   public void removeHttpService(HttpService svc)
   {
      _httpService = null;
   }

   /**
    * This is a good time to register all our content with the HttpService.
    */
   public void activate()
   {
      try
      {
         Activator.getLog().info("RequestUISessionId: " + REQUESTUISESSIONID_ALIAS_INTERNAL);
         RequestUISessionIdServlet requestUISessionId = new RequestUISessionIdServlet();
         ReleaseUISessionIdServlet releaseUISessionId = new ReleaseUISessionIdServlet();
         SetDeviceContextServlet setDeviceContext = new SetDeviceContextServlet();
         GetDeviceSettingsServlet getDeviceSettings = new GetDeviceSettingsServlet();
         ExecuteJobServlet executeJob = new ExecuteJobServlet();
         GetJobSettableElementsServlet getJobSettableElements = new GetJobSettableElementsServlet();
         GetJobStatusServlet getJobStatus = new GetJobStatusServlet();
         
         // Now register all the content
         _httpService.registerServlet(REQUESTUISESSIONID_ALIAS_INTERNAL, requestUISessionId, null, null);
         _httpService.registerServlet(RELEASEUISESSIONID_ALIAS_INTERNAL, releaseUISessionId, null, null);
         _httpService.registerServlet(SETDEVICECONTEXT_ALIAS_INTERNAL, setDeviceContext, null, null);
         _httpService.registerServlet(GETDEVICESETTINGS_ALIAS_INTERNAL, getDeviceSettings, null, null);
         _httpService.registerServlet(EXECUTEJOB_ALIAS_INTERNAL, executeJob, null, null);
         _httpService.registerServlet(GETJOBSETTABLEELEMENTS_ALIAS_INTERNAL, getJobSettableElements, null, null);
         _httpService.registerServlet(GETJOBSTATUS_ALIAS_INTERNAL, getJobStatus, null, null);
      }
      catch (Exception e)
      {
         Activator.getLog().info("Problem registering web content", e);
      }
   }

   /**
    * We are about to go down. If we still have an HttpService, unregister
    * all our content.
    */
   public void deactivate()
   {
      if (_httpService != null)
      {
         try
         {
            _httpService.unregister(REQUESTUISESSIONID_ALIAS_INTERNAL);
            _httpService.unregister(RELEASEUISESSIONID_ALIAS_INTERNAL);
         }
         catch (Exception e)
         {
            Activator.getLog().info("Problem unregistering web content", e);
         }
      }
   }

}
