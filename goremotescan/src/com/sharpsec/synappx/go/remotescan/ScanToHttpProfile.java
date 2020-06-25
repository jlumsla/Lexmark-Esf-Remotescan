package com.sharpsec.synappx.go.remotescan;

import java.util.Dictionary;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.ungoverned.gravity.servicebinder.ServiceBinderContext;

import com.lexmark.core.IntegerElem;
import com.lexmark.prtapp.characteristics.DeviceCharacteristicsService;
import com.lexmark.prtapp.profile.BasicProfileContext;
import com.lexmark.prtapp.profile.PrtappProfile;
import com.lexmark.prtapp.profile.PrtappProfileException;
import com.lexmark.prtapp.prompt.PromptException;
import com.lexmark.prtapp.prompt.PromptFactory;
import com.lexmark.prtapp.settings.SettingDefinitionMap;
import com.lexmark.prtapp.settings.SettingsAdmin;
import com.lexmark.prtapp.smbclient.AuthOptions;
import com.lexmark.prtapp.smbclient.SmbClient;
import com.lexmark.prtapp.smbclient.SmbClientService;
import com.lexmark.prtapp.smbclient.SmbConfig.ConfigBuilder;
import com.lexmark.prtapp.std.prompts.MessagePrompt;
import com.lexmark.prtapp.util.BundleUtilities;
import com.lexmark.prtapp.util.Messages;
import com.lexmark.ui.DocumentWorkflow;
import com.lexmark.ui.WorkflowFactory;
import com.lexmark.ui.WorkflowSetting;

/**
 * Main application class that provides the scan to share
 * example profile.
 */
public class ScanToHttpProfile implements PrtappProfile, ManagedService
{
   private final String appName;
   
   private SmbClientService smbClientService = null;
   private SettingsAdmin settingsAdmin = null;
   private DeviceCharacteristicsService characteristicsService = null;

   /**
    * Constructor called by Service Binder. We need to keep track of the
    * context, since it allows us access to our own bundle.
    */
   public ScanToHttpProfile(ServiceBinderContext sbc)
   {
      // This is a trick to ensure that anywhere we have to use this bundle's
      // name, it is retrieved from the bundle itself and so cannot get out
      // of synch
//      appName = BundleUtilities.getName(sbc.getBundleContext().getBundle());
      ServiceBinderContext junk = sbc;
      appName = "";
   }

   /* (non-Javadoc)
    * @see com.lexmark.prtapp.profile.PrtappProfile#getId()
    */
   public String getId()
   {
      return "ScanToSmbProfile";
   }

   /* (non-Javadoc)
    * @see com.lexmark.prtapp.profile.PrtappProfile#getName(java.util.Locale)
    */
   public String getName(Locale locale)
   {
      Messages messages = new Messages("Resources", locale, getClass()
            .getClassLoader());
      return messages.getString("application.name");
   }

   /* (non-Javadoc)
    * @see com.lexmark.prtapp.profile.PrtappProfile#getShortcut()
    */
   public int getShortcut()
   {
      return 0;
   }

   /* (non-Javadoc)
    * @see com.lexmark.prtapp.profile.PrtappProfile#showInHeldJobsList()
    */
   public boolean showInHeldJobsList()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see com.lexmark.prtapp.profile.Profile#go(com.lexmark.prtapp.profile.BasicProfileContext)
    */
   public void go(BasicProfileContext context) throws PrtappProfileException
   {
      // Set up info about our share and path.
      Activator.getLog().info("Executing go start");

//      configBuilder.setAuthType(AuthOptions.NTLMv2);

//      PromptFactory pf = context.getPromptFactory();
//      WorkflowFactory wf = context.getWorkflowFactory();
      HttpClient client = null;
      
      try
      {
         Activator.getLog().info("go0");
         // If the machine does not have a scanner, we can't scan
         String hasScanner = characteristicsService.get("scanner.hasScanner");
         
         Activator.getLog().info("go1");
         
         if(hasScanner.equals("false"))
         {
//            MessagePrompt mp = (MessagePrompt)pf.newPrompt(MessagePrompt.ID);
//            mp.setMessage("This example requires a scanner.");
//            context.displayPrompt(mp);
            return;
         }
         
         Activator.getLog().info("go3");
         
         DocumentWorkflow docWorkflow = (DocumentWorkflow)wf.create(WorkflowFactory.DOCUMENT);
         
         // This will allow fancy options such as OCR and MRC to be selected. This
         // needs to be done with care since these options can make processing the scan
         // much more time consuming. It is safe to do it in this app since
         // it does not wait for the scan consumer to complete before exiting the profile.
         // (See the User's Guide -> Workflows -> OCR for more info on this.)
         WorkflowSetting bgProcessing = docWorkflow.getSettingCollection().getSetting("supportsBackgroundProcessing");
         bgProcessing.setInfo(new IntegerElem(1));
         
         context.displayWorkflow(docWorkflow);
         
         Activator.getLog().info("go4");
         
         WorkflowSetting fileFormatSetting = docWorkflow.getSettingCollection().getSetting("fileFormat");
         
         FileShareHandler fsh = new FileShareHandler(client, fileFormatSetting.getInfo().intValue());
         docWorkflow.setConsumer(fsh);
         
         context.startWorkflow(docWorkflow, BasicProfileContext.WAIT_FOR_SCAN_COMPLETE);
      }
      catch (PromptException e)
      {
         // This is somewhat normal - will happen on a cancel, so don't log the whole exception
         Activator.getLog().info("Prompt stopped: " + e.getMessage());
      }
      catch(Exception e)
      {
         Activator.getLog().info("Problem running profile", e);
      }
      finally
      {
         // It is crucial to close the SMB client, otherwise system resources are left
         // open!
//         if(client != null) client.close();
      }
   }

   /**
    * ServiceBinder method - called when SettingsAdmin arrives
    */
   public void addSettingsAdmin(SettingsAdmin svc)
   {
      settingsAdmin = svc;
   }

   /**
    * ServiceBinder method - called when SettingsAdmin leaves town
    */
   public void removeSettingsAdmin(SettingsAdmin svc)
   {
      settingsAdmin = null;
   }
   
   /**
    * ServiceBinder method - called when SmbClientService arrives
    */
   public void addSmbClientService(SmbClientService svc)
   {
      smbClientService = svc;
   }
   
   /**
    * ServiceBinder method - called when SmbClientService leaves town
    */
   public void removeSmbClientService(SmbClientService svc)
   {
      smbClientService = null;
   }

   /**
    * ServiceBinder method - called when DeviceCharacteristicsService arrives
    */
   public void addDeviceCharacteristicsService(DeviceCharacteristicsService svc)
   {
      characteristicsService = svc;
   }   
   
   /**
    * ServiceBinder method - called when DeviceCharacteristicsService leaves town
    */
   public void removeDeviceCharacteristicsService(DeviceCharacteristicsService svc)
   {
      characteristicsService = null;
   }   
   
   /* (non-Javadoc)
    * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
    */
   public void updated(Dictionary arg0) throws ConfigurationException
   {
      // This is required for an app that receives settings. But we just
      // access the settings through Settings Admin, so no need to do
      // anything here.
   }
}

