/*
 * MIT License
 *
 * Copyright (C) 2020-2022  offa
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

import hudson.model.ManagementLink;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@ExtendWith(MockitoExtension.class)
class MaintenanceModeLinkTest
{
    @Mock
    private StaplerRequest req;
    @Mock
    private StaplerResponse resp;

    @Test
    void postRequired()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getRequiresPOST()).isTrue();
    }

    @Test
    void stableUrlName()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getUrlName()).isEqualTo("maintenance-mode");
    }

    @Test
    void supportsCategory()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getCategory()).isEqualTo(ManagementLink.Category.TOOLS);
    }

    @Test
    void requiredPermission()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getRequiredPermission()).isEqualTo(Jenkins.ADMINISTER);
    }

    @Test
    void isInactiveByDefault()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.isActive()).isFalse();
    }

    @Test
    void messageIsNotSetByDefault()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getReason()).isNull();
    }

    @Test
    void enableModeChangesState() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        mockFormParameter("");

        assertThat(link.isActive()).isFalse();
        link.doEnableMode(req, resp);
        assertThat(link.isActive()).isTrue();
        verify(link).setMaintenanceMode(true, null);
    }

    @Test
    void disableModeChangesState() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);

        assertThat(link.isActive()).isFalse();
        link.doDisableMode(req, resp);
        assertThat(link.isActive()).isFalse();
        verify(link).setMaintenanceMode(false, null);
    }

    @Test
    void enableModeSetsMessageIfPassed() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        mockFormParameter("a reason text");

        assertThat(link.isActive()).isFalse();
        link.doEnableMode(req, resp);
        assertThat(link.isActive()).isTrue();
        verify(link).setMaintenanceMode(true, "a reason text");
    }

    @Test
    void enableModeUpdatesMessageIfAlreadySet() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);

        mockFormParameter("first reason text");
        link.doEnableMode(req, resp);
        verify(link).setMaintenanceMode(true, "first reason text");

        mockFormParameter("second reason text");
        link.doEnableMode(req, resp);
        verify(link).setMaintenanceMode(true, "second reason text");
    }

    @Test
    void enableModeSavesState() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        mockFormParameter("");

        link.doEnableMode(req, resp);
        verify(link).save();
    }

    @Test
    void disableModeSavesState() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);

        link.doDisableMode(req, resp);
        verify(link).save();
    }

    @Test
    void loadStateRestoresState() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).load();
        doNothing().when(link).setMaintenanceMode(anyBoolean(), any());
        link.loadState();
        verify(link).setMaintenanceMode(false, null);
    }

    @Test
    void enableModeRedirects() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        when(req.getContextPath()).thenReturn("redirect-url");
        mockFormParameter("");

        link.doEnableMode(req, resp);
        verify(resp).sendRedirect2("redirect-url");
    }

    @Test
    void disableModeRedirects() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        when(req.getContextPath()).thenReturn("redirect-url");

        link.doDisableMode(req, resp);
        verify(resp).sendRedirect2("redirect-url");
    }

    @Test
    void enableModeChecksPermission() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);
        mockFormParameter("");

        link.doEnableMode(req, resp);
        verify(link).checkPermission(Jenkins.ADMINISTER);
    }

    @Test
    void disableModeChecksPermission() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        mockForSave(link);

        link.doDisableMode(req, resp);
        verify(link).checkPermission(Jenkins.ADMINISTER);
    }

    private void mockFormParameter(String reason) throws ServletException
    {
        final JSONObject obj = new JSONObject();
        obj.put("reasonText", reason);
        when(req.getSubmittedForm()).thenReturn(obj);
    }

    private void mockForSave(MaintenanceModeLink link) throws IOException
    {
        doNothing().when(link).save();
        doNothing().when(link).setMaintenanceMode(anyBoolean(), any());
        doNothing().when(link).checkPermission(any(Permission.class));
    }
}