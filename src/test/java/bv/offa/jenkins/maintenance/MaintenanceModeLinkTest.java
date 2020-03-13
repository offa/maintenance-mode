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

import org.acegisecurity.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.kohsuke.stapler.StaplerRequest;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MaintenanceModeLinkTest
{
    @Test
    void postRequired()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.getRequiresPOST()).isTrue();
    }

    @Test
    void isInactiveByDefault()
    {
        final MaintenanceModeLink link = new MaintenanceModeLink();
        assertThat(link.isActive()).isFalse();
    }

    @Test
    void toggleChangesState()
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).checkPermission();

        final StaplerRequest req = mock(StaplerRequest.class);
        assertThat(link.isActive()).isFalse();
        link.doToggleMode(req);
        assertThat(link.isActive()).isTrue();
    }

    @Test
    void toggleChecksPermission()
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doNothing().when(link).checkPermission();

        final StaplerRequest req = mock(StaplerRequest.class);
        link.doToggleMode(req);
        verify(link).checkPermission();
    }

    @Test
    void toggleFailsWithoutPermission()
    {
        final MaintenanceModeLink link = spy(new MaintenanceModeLink());
        doThrow(AccessDeniedException.class).when(link).checkPermission();
        final StaplerRequest req = mock(StaplerRequest.class);
        assertThrows(AccessDeniedException.class, () -> link.doToggleMode(req));
    }

}