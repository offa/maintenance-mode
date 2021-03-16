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

import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@ExtendWith(MockitoExtension.class)
class MaintenanceModeLinkTest
{
    private static final HttpRedirect IGNORE_REDIRECT = new HttpRedirect("ignore");
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
    void toggleChangesState() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).save();
        doReturn(IGNORE_REDIRECT).when(link).setMaintenanceMode(anyBoolean());

        assertThat(link.isActive()).isFalse();
        link.doToggleMode(req, resp);
        assertThat(link.isActive()).isTrue();
        verify(link).setMaintenanceMode(true);
    }

    @Test
    void toggleChangesStateBackWhenCalledTwice() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).save();
        doReturn(IGNORE_REDIRECT).when(link).setMaintenanceMode(anyBoolean());

        InOrder inOrder = inOrder(link);
        assertThat(link.isActive()).isFalse();
        link.doToggleMode(req, resp);
        assertThat(link.isActive()).isTrue();
        inOrder.verify(link).setMaintenanceMode(true);
        link.doToggleMode(req, resp);
        assertThat(link.isActive()).isFalse();
        inOrder.verify(link).setMaintenanceMode(false);
    }

    @Test
    void toggleSavesState() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).save();
        doReturn(IGNORE_REDIRECT).when(link).setMaintenanceMode(anyBoolean());

        link.doToggleMode(req, resp);
        verify(link).save();
    }

    @Test
    void loadStateRestoresState() throws IOException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).load();
        doReturn(IGNORE_REDIRECT).when(link).setMaintenanceMode(anyBoolean());
        link.loadState();
        verify(link).setMaintenanceMode(false);
    }

    @Test
    void toggleRedirects() throws IOException, ServletException
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).save();
        final HttpRedirect redirect = new HttpRedirect("new-url-for-redirect");
        doReturn(redirect).when(link).setMaintenanceMode(anyBoolean());

        link.doToggleMode(req, resp);
        verify(resp).sendRedirect(anyInt(), eq("new-url-for-redirect"));
    }

}