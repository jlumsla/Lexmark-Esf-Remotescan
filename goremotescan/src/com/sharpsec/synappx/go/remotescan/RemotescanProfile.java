
package com.sharpsec.synappx.go.remotescan;

import org.ungoverned.gravity.servicebinder.ServiceBinderContext;

import com.lexmark.prtapp.profile.BasicProfileContext;
import com.lexmark.prtapp.profile.PrtappProfile;
import com.lexmark.prtapp.profile.PrtappProfileException;
import com.lexmark.prtapp.std.prompts.MessagePrompt;
import com.lexmark.prtapp.prompt.PromptException;
import com.lexmark.prtapp.prompt.PromptFactory;
import com.lexmark.prtapp.prompt.PromptFactoryException;
import com.lexmark.prtapp.util.Messages;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import com.lexmark.prtapp.settings.SettingsAdmin;

import java.util.Dictionary;
import java.util.Enumeration;

import java.util.Locale;

public class RemotescanProfile implements PrtappProfile, ManagedService
{
   // ServiceBinder context - we can get a BundleContext from it
   private ServiceBinderContext _sbc = null;
   //private RemotescanProfileService _remotescanprofileservice = null;
   private SettingsAdmin _settingsAdmin = null;

   /**
    * Constructor called by Service Binder. We need to keep track of the
    * context, since it allows us access to our own bundle.
    */
   public RemotescanProfile(ServiceBinderContext sbc)
   {
      _sbc = sbc;
   }

   public String getId()
   {
      return "RemotescanProfile";
   }

   public String getName(Locale locale)
   {
      Messages messages = new Messages("Resources", locale,
            getClass().getClassLoader());
      return messages.getString("profile.name");
   }

   public int getShortcut()
   {
      return 0;
   }

   // This only has an effect for framework 2.0 and above   
   public boolean showInHeldJobsList()
   {
      return true;
   }

   public void go(BasicProfileContext context) throws PrtappProfileException
   {

      // TODO : Implement your business logic here.
      context.showPleaseWait(true);
      PromptFactory pf = context.getPromptFactory();
      MessagePrompt mp1;
      try
      {
         Messages message = new Messages("Resources", context.getLocale(),
               getClass().getClassLoader());
         String label = message.getString("prompt.label");
         mp1 = (MessagePrompt) pf.newPrompt(MessagePrompt.ID);
         mp1.setLabel(label);
         context.displayPrompt(mp1);
      }
      catch (PromptFactoryException e)
      {
         Activator.getLog().debug("Exception thrown", e);
      }
      catch (PromptException e)
      {
         Activator.getLog().debug("Exception thrown", e);
      }

   }

   /*
   public void addRemotescanProfileService(RemotescanProfileService service)
   {
      _remotescanprofileservice = service;
   
   }
   
   public void removeRemotescanProfileService(RemotescanProfileService service)
   {
      // This service just went away, we shouldn't rely on any
      // of its methods still being valid.
   
      _remotescanprofileservice = null;
   
   }
   */
   /**
    * This is a ManagedService function that gets called any time the app
    * receives settings.  This will happen:
    * <ul><li>On startup</li>
    * <li>When settings are changed (in the web page or through an external
    * app such as MarkVision</li></ul>
    */
   public void updated(Dictionary settings) throws ConfigurationException
   {
      // NOTE that the settings passed in can be null.  This will happen
      // if the settings were deleted or were never set to begin with.
      if (settings != null)
      {

         // Log out all of the settings that we got.
         Activator.getLog().debug("We got new settings!");
         Enumeration elems = settings.keys();
         while (elems.hasMoreElements())
         {
            String key = elems.nextElement().toString();
            Object value = settings.get(key);
            Activator.getLog().debug("\t" + key + " = " + value);
         }
      }
   }

   /**
    * ServiceBinder method - called when SettingsAdmin arrives
    */
   public void addSettingsAdmin(SettingsAdmin svc)
   {
      _settingsAdmin = svc;
   }

   /**
    * ServiceBinder method - called when SettingsAdmin leaves town
    */
   public void removeSettingsAdmin(SettingsAdmin svc)
   {
      _settingsAdmin = null;
   }
}
