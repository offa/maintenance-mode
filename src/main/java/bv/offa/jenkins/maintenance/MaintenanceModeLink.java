/*
 * MIT License
 *
 * Copyright (c) 2020-2021 offa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bv.offa.jenkins.maintenance;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.BulkChange;
import hudson.Extension;
import hudson.XmlFile;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.ManagementLink;
import hudson.model.Saveable;
import hudson.security.Permission;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.verb.POST;

import javax.annotation.CheckForNull;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

@Extension
public class MaintenanceModeLink extends ManagementLink implements Saveable
{
    private static final XStream2 XSTREAM = new XStream2();
    private volatile boolean active;


    @CheckForNull
    @Override
    public String getIconFileName()
    {
        return isActive() ? "error.png" : "accept.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName()
    {
        return isActive() ? Messages.MaintenanceModeLink_displayname_enabled() : Messages.MaintenanceModeLink_displayname_disabled();
    }

    @Override
    public String getDescription()
    {
        return Messages.MaintenanceModeLink_description();
    }

    @CheckForNull
    @Override
    public String getUrlName()
    {
        return "maintenance-mode";
    }

    @Override
    public boolean getRequiresPOST()
    {
        return true;
    }

    @NonNull
    @Override
    public Category getCategory()
    {
        return Category.TOOLS;
    }

    @NonNull
    @Override
    public Permission getRequiredPermission()
    {
        return Jenkins.ADMINISTER;
    }

    public boolean isActive()
    {
        return active;
    }

    @POST
    public synchronized void doToggleMode(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException
    {
        checkPermission(getRequiredPermission());
        active = !active;
        save();
        setMaintenanceMode(active).generateResponse(req, resp, null);
    }

    @Override
    public void save() throws IOException
    {
        if (!BulkChange.contains(this))
        {
            getConfigFile().write(this);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Initializer(after = InitMilestone.JOB_LOADED)
    public void loadState() throws IOException
    {
        load();
        setMaintenanceMode(active);
    }

    protected HttpRedirect setMaintenanceMode(boolean enabled)
    {
        if (enabled)
        {
            return Jenkins.get().doQuietDown();
        }
        return Jenkins.get().doCancelQuietDown();
    }

    protected void checkPermission(Permission permission)
    {
        Jenkins.get().checkPermission(permission);
    }

    protected void load() throws IOException
    {
        final XmlFile configFile = getConfigFile();

        if (configFile.exists())
        {
            configFile.unmarshal(this);
        }
    }

    private XmlFile getConfigFile()
    {
        return new XmlFile(XSTREAM, new File(Jenkins.get().getRootDir(), "bv.offa.jenkins.maintenancemode.xml"));
    }

}
