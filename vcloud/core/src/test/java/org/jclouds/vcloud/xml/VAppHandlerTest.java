/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.SortedSet;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;

/**
 * Tests behavior of {@code VAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VAppHandlerTest")
public class VAppHandlerTest extends BaseHandlerTest {
   @Test(enabled=false)
   public void testHosting() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp-hosting.xml");

      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      ListMultimap<String, InetAddress> networkToAddresses = ImmutableListMultimap
               .<String, InetAddress> of("Network 1", InetAddress.getByName("204.12.59.147"));

      VirtualSystem system = new VirtualSystem(0, "Virtual Hardware Family", "SimpleVM", "vmx-07");

      SortedSet<ResourceAllocation> resourceAllocations = ImmutableSortedSet
               .<ResourceAllocation> naturalOrder().add(
                        new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs",
                                 ResourceType.PROCESSOR, null, null, null, null, null, null, 1,
                                 "hertz * 10^6"),
                        new ResourceAllocation(2, "512MB of memory", "Memory Size",
                                 ResourceType.MEMORY, null, null, null, null, null, null, 512,
                                 "byte * 2^20")).add(

                        new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller",
                                 ResourceType.SCSI_CONTROLLER, "lsilogic", null, 0, null, null,
                                 null, 1, null)).add(

                        new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE,
                                 null, "20971520", null, 0, 3, null, 20971520, "byte * 2^20"))
               .build();

      VApp expects = new VAppImpl("188849-74", "188849-74", URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vapp/188849-74"), VAppStatus.ON,
               new Long(20971520),null,
               networkToAddresses, null, system, resourceAllocations);

      assertEquals(result, expects);
   }
   public void testInstantiated() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/instantiatedvapp.xml");

     VApp result = factory.create(
               injector.getInstance(VAppHandler.class)).parse(is);

     VApp expects = new VAppImpl("10", "centos53", URI
               .create("http://10.150.4.49/api/v0.8/vApp/10"),
               VAppStatus.RESOLVED, 123456789l, new NamedResourceImpl("4", null,
                     "application/vnd.vmware.vcloud.vdc+xml", URI
                     .create("http://10.150.4.49/api/v0.8/vdc/4")),
                     ImmutableListMultimap.<String, InetAddress> of(),
               null, null, ImmutableSet.<ResourceAllocation>of());
      assertEquals(result, expects);
   }
   // TODO why does this fail?
   @Test(enabled = false)
   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp.xml");

      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      ListMultimap<String, InetAddress> networkToAddresses = ImmutableListMultimap
              .<String, InetAddress> of();

      VirtualSystem system = new VirtualSystem(0, "Virtual Hardware Family", "Oracle", "vmx-07");

      SortedSet<ResourceAllocation> resourceAllocations = ImmutableSortedSet
               .<ResourceAllocation> naturalOrder().add(
                        new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs",
                                 ResourceType.PROCESSOR, null, null, null, null, null, null, 1,
                                 "hertz * 10^6"),
                        new ResourceAllocation(2, "16MB of memory", "Memory Size",
                                 ResourceType.MEMORY, null, null, null, null, null, null, 16,
                                 "byte * 2^20")).add(
                        new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller",
                                 ResourceType.SCSI_CONTROLLER, "lsilogic", null, 0, null, null,
                                 null, 1, null)).add(
                        new ResourceAllocation(8, "Network Adapter 1", "PCNet32 ethernet adapter on \"Internal\" network",
                                 ResourceType.ETHERNET_ADAPTER, "PCNet32", null, null, 7, null,
                                 true, 1, null)).add(                                		 
                        new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE,
                                 null, "104857", null, 0, 3, null, 104857, "byte * 2^20"))
               .build();

      VApp expects = new VAppImpl("4", "Oracle", URI
               .create("http://10.150.4.49/api/v0.8/vApp/4"), VAppStatus.ON,
               new Long(104857), new NamedResourceImpl("32", null,
                     "application/vnd.vmware.vcloud.vdc+xml", URI
                     .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")),
                     networkToAddresses, "Other Linux (32-bit)", system, resourceAllocations);

      assertEquals(result, expects);

   }
}
