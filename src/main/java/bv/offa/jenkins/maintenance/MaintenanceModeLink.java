/*
 * MIT License
 *
 * Copyright (c) 2020 offa
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

import hudson.Extension;
import hudson.model.ManagementLink;
import hudson.util.FormApply;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.annotation.CheckForNull;

@Extension
public class MaintenanceModeLink extends ManagementLink
{
    private volatile boolean active;

    @CheckForNull
    @Override
    public String getIconFileName()
    {
        return "gear2.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName()
    {
        return "Maintenance Mode";
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

    public boolean isActive()
    {
        return active;
    }

    @RequirePOST
    public synchronized HttpResponse doToggleMode(StaplerRequest req)
    {
        checkPermission();
        active = !active;
        return FormApply.success(".");
    }

    protected void checkPermission()
    {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    }
}
