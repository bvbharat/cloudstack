// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloud.agent.manager.ClusteredAgentManagerImpl;
import com.cloud.alert.AlertManagerImpl;
import com.cloud.alert.dao.AlertDaoImpl;
import com.cloud.api.query.QueryManagerImpl;
import com.cloud.api.query.dao.AccountJoinDaoImpl;
import com.cloud.api.query.dao.AsyncJobJoinDaoImpl;
import com.cloud.api.query.dao.DataCenterJoinDaoImpl;
import com.cloud.api.query.dao.DiskOfferingJoinDaoImpl;
import com.cloud.api.query.dao.ServiceOfferingJoinDaoImpl;
import com.cloud.api.query.dao.DomainRouterJoinDaoImpl;
import com.cloud.api.query.dao.InstanceGroupJoinDaoImpl;
import com.cloud.api.query.dao.ProjectAccountJoinDaoImpl;
import com.cloud.api.query.dao.ProjectInvitationJoinDaoImpl;
import com.cloud.api.query.dao.ProjectJoinDaoImpl;
import com.cloud.api.query.dao.ResourceTagJoinDaoImpl;
import com.cloud.api.query.dao.SecurityGroupJoinDaoImpl;
import com.cloud.api.query.dao.StoragePoolJoinDaoImpl;
import com.cloud.api.query.dao.UserAccountJoinDaoImpl;
import com.cloud.api.query.dao.UserVmJoinDaoImpl;
import com.cloud.api.query.dao.HostJoinDaoImpl;
import com.cloud.api.query.dao.VolumeJoinDaoImpl;
import com.cloud.async.AsyncJobExecutorContextImpl;
import com.cloud.async.AsyncJobManagerImpl;
import com.cloud.async.SyncQueueManagerImpl;
import com.cloud.async.dao.AsyncJobDaoImpl;
import com.cloud.async.dao.SyncQueueDaoImpl;
import com.cloud.async.dao.SyncQueueItemDaoImpl;
import com.cloud.capacity.CapacityManagerImpl;
import com.cloud.capacity.dao.CapacityDaoImpl;
import com.cloud.certificate.dao.CertificateDaoImpl;
import com.cloud.cluster.CheckPointManagerImpl;
import com.cloud.cluster.ClusterFenceManagerImpl;
import com.cloud.cluster.ClusterManagerImpl;
import com.cloud.cluster.agentlb.dao.HostTransferMapDaoImpl;
import com.cloud.cluster.dao.ManagementServerHostDaoImpl;
import com.cloud.cluster.dao.ManagementServerHostPeerDaoImpl;
import com.cloud.cluster.dao.StackMaidDaoImpl;
import com.cloud.configuration.dao.ConfigurationDaoImpl;
import com.cloud.configuration.dao.ResourceCountDaoImpl;
import com.cloud.configuration.dao.ResourceLimitDaoImpl;
import com.cloud.consoleproxy.ConsoleProxyManagerImpl;
import com.cloud.dao.EntityManager;
import com.cloud.dao.EntityManagerImpl;
import com.cloud.dc.ClusterDetailsDaoImpl;
import com.cloud.dc.dao.AccountVlanMapDaoImpl;
import com.cloud.dc.dao.ClusterDaoImpl;
import com.cloud.dc.dao.ClusterVSMMapDaoImpl;
import com.cloud.dc.dao.DataCenterDaoImpl;
import com.cloud.dc.dao.DataCenterIpAddressDaoImpl;
import com.cloud.dc.dao.DcDetailsDaoImpl;
import com.cloud.dc.dao.HostPodDaoImpl;
import com.cloud.dc.dao.PodVlanMapDaoImpl;
import com.cloud.dc.dao.StorageNetworkIpAddressDaoImpl;
import com.cloud.dc.dao.StorageNetworkIpRangeDaoImpl;
import com.cloud.dc.dao.VlanDaoImpl;
import com.cloud.domain.dao.DomainDaoImpl;
import com.cloud.event.dao.EventDaoImpl;
import com.cloud.event.dao.UsageEventDaoImpl;
import com.cloud.ha.HighAvailabilityManagerImpl;
import com.cloud.ha.dao.HighAvailabilityDaoImpl;
import com.cloud.host.dao.HostDaoImpl;
import com.cloud.host.dao.HostDetailsDaoImpl;
import com.cloud.host.dao.HostTagsDaoImpl;
import com.cloud.hypervisor.HypervisorGuruManagerImpl;
import com.cloud.hypervisor.dao.HypervisorCapabilitiesDaoImpl;
import com.cloud.keystore.KeystoreDaoImpl;
import com.cloud.keystore.KeystoreManagerImpl;
import com.cloud.maint.UpgradeManagerImpl;
import com.cloud.maint.dao.AgentUpgradeDaoImpl;
import com.cloud.network.ExternalLoadBalancerUsageManagerImpl;
import com.cloud.network.NetworkManagerImpl;
import com.cloud.network.NetworkModelImpl;
import com.cloud.network.NetworkServiceImpl;
import com.cloud.network.StorageNetworkManagerImpl;
import com.cloud.network.as.AutoScaleManagerImpl;
import com.cloud.network.as.dao.AutoScalePolicyConditionMapDaoImpl;
import com.cloud.network.as.dao.AutoScalePolicyDaoImpl;
import com.cloud.network.as.dao.AutoScaleVmGroupDaoImpl;
import com.cloud.network.as.dao.AutoScaleVmGroupPolicyMapDaoImpl;
import com.cloud.network.as.dao.AutoScaleVmProfileDaoImpl;
import com.cloud.network.as.dao.ConditionDaoImpl;
import com.cloud.network.as.dao.CounterDaoImpl;
import com.cloud.network.dao.ExternalFirewallDeviceDaoImpl;
import com.cloud.network.dao.ExternalLoadBalancerDeviceDaoImpl;
import com.cloud.network.dao.FirewallRulesCidrsDaoImpl;
import com.cloud.network.dao.FirewallRulesDaoImpl;
import com.cloud.network.dao.IPAddressDaoImpl;
import com.cloud.network.dao.InlineLoadBalancerNicMapDaoImpl;
import com.cloud.network.dao.LBStickinessPolicyDaoImpl;
import com.cloud.network.dao.LoadBalancerDaoImpl;
import com.cloud.network.dao.LoadBalancerVMMapDaoImpl;
import com.cloud.network.dao.NetworkDaoImpl;
import com.cloud.network.dao.NetworkDomainDaoImpl;
import com.cloud.network.dao.NetworkExternalFirewallDaoImpl;
import com.cloud.network.dao.NetworkExternalLoadBalancerDaoImpl;
import com.cloud.network.dao.NetworkRuleConfigDaoImpl;
import com.cloud.network.dao.NetworkServiceMapDaoImpl;
import com.cloud.network.dao.PhysicalNetworkDaoImpl;
import com.cloud.network.dao.PhysicalNetworkServiceProviderDaoImpl;
import com.cloud.network.dao.PhysicalNetworkTrafficTypeDaoImpl;
import com.cloud.network.dao.PortProfileDaoImpl;
import com.cloud.network.dao.RemoteAccessVpnDaoImpl;
import com.cloud.network.dao.Site2SiteCustomerGatewayDaoImpl;
import com.cloud.network.dao.Site2SiteVpnConnectionDaoImpl;
import com.cloud.network.dao.Site2SiteVpnGatewayDaoImpl;
import com.cloud.network.dao.VirtualRouterProviderDaoImpl;
import com.cloud.network.dao.VpnUserDaoImpl;
import com.cloud.network.element.VirtualRouterElement;
import com.cloud.network.element.VirtualRouterElementService;
import com.cloud.network.firewall.FirewallManagerImpl;
import com.cloud.network.lb.LoadBalancingRulesManagerImpl;
import com.cloud.network.router.VpcVirtualNetworkApplianceManagerImpl;
import com.cloud.network.rules.RulesManagerImpl;
import com.cloud.network.rules.dao.PortForwardingRulesDaoImpl;
import com.cloud.network.security.SecurityGroupManagerImpl2;
import com.cloud.network.security.dao.SecurityGroupDaoImpl;
import com.cloud.network.security.dao.SecurityGroupRuleDaoImpl;
import com.cloud.network.security.dao.SecurityGroupRulesDaoImpl;
import com.cloud.network.security.dao.SecurityGroupVMMapDaoImpl;
import com.cloud.network.security.dao.SecurityGroupWorkDaoImpl;
import com.cloud.network.security.dao.VmRulesetLogDaoImpl;
import com.cloud.network.vpc.NetworkACLManagerImpl;
import com.cloud.network.vpc.VpcManagerImpl;
import com.cloud.network.vpc.dao.PrivateIpDaoImpl;
import com.cloud.network.vpc.dao.StaticRouteDaoImpl;
import com.cloud.network.vpc.dao.VpcDaoImpl;
import com.cloud.network.vpc.dao.VpcGatewayDaoImpl;
import com.cloud.network.vpc.dao.VpcOfferingDaoImpl;
import com.cloud.network.vpc.dao.VpcOfferingServiceMapDaoImpl;
import com.cloud.network.vpn.RemoteAccessVpnManagerImpl;
import com.cloud.network.vpn.Site2SiteVpnManagerImpl;
import com.cloud.offerings.dao.NetworkOfferingDaoImpl;
import com.cloud.offerings.dao.NetworkOfferingServiceMapDaoImpl;
import com.cloud.projects.ProjectManagerImpl;
import com.cloud.projects.dao.ProjectAccountDaoImpl;
import com.cloud.projects.dao.ProjectDaoImpl;
import com.cloud.projects.dao.ProjectInvitationDaoImpl;
import com.cloud.resource.ResourceManagerImpl;
import com.cloud.resourcelimit.ResourceLimitManagerImpl;
import com.cloud.service.dao.ServiceOfferingDaoImpl;
import com.cloud.storage.OCFS2ManagerImpl;
import com.cloud.storage.StorageManagerImpl;
import com.cloud.storage.dao.DiskOfferingDaoImpl;
import com.cloud.storage.dao.GuestOSCategoryDaoImpl;
import com.cloud.storage.dao.GuestOSDaoImpl;
import com.cloud.storage.dao.LaunchPermissionDaoImpl;
import com.cloud.storage.dao.S3DaoImpl;
import com.cloud.storage.dao.SnapshotDaoImpl;
import com.cloud.storage.dao.SnapshotPolicyDaoImpl;
import com.cloud.storage.dao.SnapshotScheduleDaoImpl;
import com.cloud.storage.dao.StoragePoolDaoImpl;
import com.cloud.storage.dao.StoragePoolHostDaoImpl;
import com.cloud.storage.dao.StoragePoolWorkDaoImpl;
import com.cloud.storage.dao.SwiftDaoImpl;
import com.cloud.storage.dao.UploadDaoImpl;
import com.cloud.storage.dao.VMTemplateDaoImpl;
import com.cloud.storage.dao.VMTemplateDetailsDaoImpl;
import com.cloud.storage.dao.VMTemplateHostDaoImpl;
import com.cloud.storage.dao.VMTemplatePoolDaoImpl;
import com.cloud.storage.dao.VMTemplateS3DaoImpl;
import com.cloud.storage.dao.VMTemplateSwiftDaoImpl;
import com.cloud.storage.dao.VMTemplateZoneDaoImpl;
import com.cloud.storage.dao.VolumeDaoImpl;
import com.cloud.storage.dao.VolumeHostDaoImpl;
import com.cloud.storage.download.DownloadMonitorImpl;
import com.cloud.storage.s3.S3ManagerImpl;
import com.cloud.storage.secondary.SecondaryStorageManagerImpl;
import com.cloud.storage.snapshot.SnapshotManagerImpl;
import com.cloud.storage.snapshot.SnapshotSchedulerImpl;
import com.cloud.storage.swift.SwiftManagerImpl;
import com.cloud.storage.upload.UploadMonitorImpl;
import com.cloud.tags.TaggedResourceManagerImpl;
import com.cloud.tags.dao.ResourceTagsDaoImpl;
import com.cloud.template.HyervisorTemplateAdapter;
import com.cloud.template.TemplateAdapter;
import com.cloud.template.TemplateAdapter.TemplateAdapterType;
import com.cloud.template.TemplateManagerImpl;
import com.cloud.user.AccountDetailsDaoImpl;
import com.cloud.user.AccountManagerImpl;
import com.cloud.user.DomainManagerImpl;
import com.cloud.user.dao.AccountDaoImpl;
import com.cloud.user.dao.SSHKeyPairDaoImpl;
import com.cloud.user.dao.UserAccountDaoImpl;
import com.cloud.user.dao.UserDaoImpl;
import com.cloud.user.dao.UserStatisticsDaoImpl;
import com.cloud.user.dao.UserStatsLogDaoImpl;
import com.cloud.utils.component.Adapter;
import com.cloud.utils.component.ComponentLibrary;
import com.cloud.utils.component.ComponentLibraryBase;
import com.cloud.utils.component.ComponentLocator.ComponentInfo;
import com.cloud.utils.component.Manager;
import com.cloud.utils.component.PluggableService;
import com.cloud.utils.db.GenericDao;
import com.cloud.uuididentity.IdentityServiceImpl;
import com.cloud.uuididentity.dao.IdentityDaoImpl;
import com.cloud.vm.ClusteredVirtualMachineManagerImpl;
import com.cloud.vm.ItWorkDaoImpl;
import com.cloud.vm.UserVmManagerImpl;
import com.cloud.vm.dao.ConsoleProxyDaoImpl;
import com.cloud.vm.dao.DomainRouterDaoImpl;
import com.cloud.vm.dao.InstanceGroupDaoImpl;
import com.cloud.vm.dao.InstanceGroupVMMapDaoImpl;
import com.cloud.vm.dao.NicDaoImpl;
import com.cloud.vm.dao.SecondaryStorageVmDaoImpl;
import com.cloud.vm.dao.UserVmDaoImpl;
import com.cloud.vm.dao.UserVmDetailsDaoImpl;
import com.cloud.vm.dao.VMInstanceDaoImpl;
import com.cloud.event.dao.EventJoinDaoImpl;



public class DefaultComponentLibrary extends ComponentLibraryBase implements ComponentLibrary {
    protected void populateDaos() {
        addDao("StackMaidDao", StackMaidDaoImpl.class);
        addDao("VMTemplateZoneDao", VMTemplateZoneDaoImpl.class);
        addDao("VMTemplateDetailsDao", VMTemplateDetailsDaoImpl.class);
        addDao("DomainRouterDao", DomainRouterDaoImpl.class);
        addDao("HostDao", HostDaoImpl.class);
        addDao("VMInstanceDao", VMInstanceDaoImpl.class);
        addDao("UserVmDao", UserVmDaoImpl.class);
        ComponentInfo<? extends GenericDao<?, ? extends Serializable>> info = addDao("ServiceOfferingDao", ServiceOfferingDaoImpl.class);
        info.addParameter("cache.size", "50");
        info.addParameter("cache.time.to.live", "600");
        info = addDao("DiskOfferingDao", DiskOfferingDaoImpl.class);
        info.addParameter("cache.size", "50");
        info.addParameter("cache.time.to.live", "600");
        info = addDao("DataCenterDao", DataCenterDaoImpl.class);
        info.addParameter("cache.size", "50");
        info.addParameter("cache.time.to.live", "600");
        info = addDao("HostPodDao", HostPodDaoImpl.class);
        info.addParameter("cache.size", "50");
        info.addParameter("cache.time.to.live", "600");
        addDao("IPAddressDao", IPAddressDaoImpl.class);
        info = addDao("VlanDao", VlanDaoImpl.class);
        info.addParameter("cache.size", "30");
        info.addParameter("cache.time.to.live", "3600");
        addDao("PodVlanMapDao", PodVlanMapDaoImpl.class);
        addDao("AccountVlanMapDao", AccountVlanMapDaoImpl.class);
        addDao("VolumeDao", VolumeDaoImpl.class);
        addDao("EventDao", EventDaoImpl.class);
        info = addDao("UserDao", UserDaoImpl.class);
        info.addParameter("cache.size", "5000");
        info.addParameter("cache.time.to.live", "300");
        addDao("UserStatisticsDao", UserStatisticsDaoImpl.class);
        addDao("UserStatsLogDao", UserStatsLogDaoImpl.class);
        addDao("FirewallRulesDao", FirewallRulesDaoImpl.class);
        addDao("LoadBalancerDao", LoadBalancerDaoImpl.class);
        addDao("NetworkRuleConfigDao", NetworkRuleConfigDaoImpl.class);
        addDao("LoadBalancerVMMapDao", LoadBalancerVMMapDaoImpl.class);
        addDao("LBStickinessPolicyDao", LBStickinessPolicyDaoImpl.class);
        addDao("CounterDao", CounterDaoImpl.class);
        addDao("ConditionDao", ConditionDaoImpl.class);
        addDao("AutoScalePolicyDao", AutoScalePolicyDaoImpl.class);
        addDao("AutoScalePolicyConditionMapDao", AutoScalePolicyConditionMapDaoImpl.class);
        addDao("AutoScaleVmProfileDao", AutoScaleVmProfileDaoImpl.class);
        addDao("AutoScaleVmGroupDao", AutoScaleVmGroupDaoImpl.class);
        addDao("AutoScaleVmGroupPolicyMapDao", AutoScaleVmGroupPolicyMapDaoImpl.class);
        addDao("DataCenterIpAddressDao", DataCenterIpAddressDaoImpl.class);
        addDao("SecurityGroupDao", SecurityGroupDaoImpl.class);
        addDao("SecurityGroupRuleDao", SecurityGroupRuleDaoImpl.class);
        addDao("SecurityGroupVMMapDao", SecurityGroupVMMapDaoImpl.class);
        addDao("SecurityGroupRulesDao", SecurityGroupRulesDaoImpl.class);
        addDao("SecurityGroupWorkDao", SecurityGroupWorkDaoImpl.class);
        addDao("VmRulesetLogDao", VmRulesetLogDaoImpl.class);
        addDao("AlertDao", AlertDaoImpl.class);
        addDao("CapacityDao", CapacityDaoImpl.class);
        addDao("DomainDao", DomainDaoImpl.class);
        addDao("AccountDao", AccountDaoImpl.class);
        addDao("ResourceLimitDao", ResourceLimitDaoImpl.class);
        addDao("ResourceCountDao", ResourceCountDaoImpl.class);
        addDao("UserAccountDao", UserAccountDaoImpl.class);
        addDao("VMTemplateHostDao", VMTemplateHostDaoImpl.class);
        addDao("VolumeHostDao", VolumeHostDaoImpl.class);
        addDao("VMTemplateSwiftDao", VMTemplateSwiftDaoImpl.class);
        addDao("VMTemplateS3Dao", VMTemplateS3DaoImpl.class);
        addDao("UploadDao", UploadDaoImpl.class);
        addDao("VMTemplatePoolDao", VMTemplatePoolDaoImpl.class);
        addDao("LaunchPermissionDao", LaunchPermissionDaoImpl.class);
        addDao("ConfigurationDao", ConfigurationDaoImpl.class);
        info = addDao("VMTemplateDao", VMTemplateDaoImpl.class);
        info.addParameter("cache.size", "100");
        info.addParameter("cache.time.to.live", "600");
        info.addParameter("routing.uniquename", "routing");
        addDao("HighAvailabilityDao", HighAvailabilityDaoImpl.class);
        addDao("ConsoleProxyDao", ConsoleProxyDaoImpl.class);
        addDao("SecondaryStorageVmDao", SecondaryStorageVmDaoImpl.class);
        addDao("ManagementServerHostDao", ManagementServerHostDaoImpl.class);
        addDao("ManagementServerHostPeerDao", ManagementServerHostPeerDaoImpl.class);
        addDao("AgentUpgradeDao", AgentUpgradeDaoImpl.class);
        addDao("SnapshotDao", SnapshotDaoImpl.class);
        addDao("AsyncJobDao", AsyncJobDaoImpl.class);
        addDao("SyncQueueDao", SyncQueueDaoImpl.class);
        addDao("SyncQueueItemDao", SyncQueueItemDaoImpl.class);
        addDao("GuestOSDao", GuestOSDaoImpl.class);
        addDao("GuestOSCategoryDao", GuestOSCategoryDaoImpl.class);
        addDao("StoragePoolDao", StoragePoolDaoImpl.class);
        addDao("StoragePoolHostDao", StoragePoolHostDaoImpl.class);
        addDao("DetailsDao", HostDetailsDaoImpl.class);
        addDao("SnapshotPolicyDao", SnapshotPolicyDaoImpl.class);
        addDao("SnapshotScheduleDao", SnapshotScheduleDaoImpl.class);
        addDao("ClusterDao", ClusterDaoImpl.class);
        addDao("CertificateDao", CertificateDaoImpl.class);
        addDao("NetworkConfigurationDao", NetworkDaoImpl.class);
        addDao("NetworkOfferingDao", NetworkOfferingDaoImpl.class);
        addDao("NicDao", NicDaoImpl.class);
        addDao("InstanceGroupDao", InstanceGroupDaoImpl.class);
        addDao("InstanceGroupJoinDao", InstanceGroupJoinDaoImpl.class);
        addDao("InstanceGroupVMMapDao", InstanceGroupVMMapDaoImpl.class);
        addDao("RemoteAccessVpnDao", RemoteAccessVpnDaoImpl.class);
        addDao("VpnUserDao", VpnUserDaoImpl.class);
        addDao("ItWorkDao", ItWorkDaoImpl.class);
        addDao("FirewallRulesDao", FirewallRulesDaoImpl.class);
        addDao("PortForwardingRulesDao", PortForwardingRulesDaoImpl.class);
        addDao("FirewallRulesCidrsDao", FirewallRulesCidrsDaoImpl.class);
        addDao("SSHKeyPairDao", SSHKeyPairDaoImpl.class);
        addDao("UsageEventDao", UsageEventDaoImpl.class);
        addDao("ClusterDetailsDao", ClusterDetailsDaoImpl.class);
        addDao("UserVmDetailsDao", UserVmDetailsDaoImpl.class);
        addDao("StoragePoolWorkDao", StoragePoolWorkDaoImpl.class);
        addDao("HostTagsDao", HostTagsDaoImpl.class);
        addDao("NetworkDomainDao", NetworkDomainDaoImpl.class);
        addDao("KeystoreDao", KeystoreDaoImpl.class);
        addDao("DcDetailsDao", DcDetailsDaoImpl.class);
        addDao("SwiftDao", SwiftDaoImpl.class);
        addDao("S3Dao", S3DaoImpl.class);
        addDao("AgentTransferMapDao", HostTransferMapDaoImpl.class);
        addDao("ProjectDao", ProjectDaoImpl.class);
        addDao("InlineLoadBalancerNicMapDao", InlineLoadBalancerNicMapDaoImpl.class);
        addDao("ProjectsAccountDao", ProjectAccountDaoImpl.class);
        addDao("ProjectInvitationDao", ProjectInvitationDaoImpl.class);
        addDao("IdentityDao", IdentityDaoImpl.class);
        addDao("AccountDetailsDao", AccountDetailsDaoImpl.class);
        addDao("NetworkOfferingServiceMapDao", NetworkOfferingServiceMapDaoImpl.class);
        info = addDao("HypervisorCapabilitiesDao",HypervisorCapabilitiesDaoImpl.class);
        info.addParameter("cache.size", "100");
        info.addParameter("cache.time.to.live", "600");
        addDao("PhysicalNetworkDao", PhysicalNetworkDaoImpl.class);
        addDao("PhysicalNetworkServiceProviderDao", PhysicalNetworkServiceProviderDaoImpl.class);
        addDao("VirtualRouterProviderDao", VirtualRouterProviderDaoImpl.class);
        addDao("ExternalLoadBalancerDeviceDao", ExternalLoadBalancerDeviceDaoImpl.class);
        addDao("ExternalFirewallDeviceDao", ExternalFirewallDeviceDaoImpl.class);
        addDao("NetworkExternalLoadBalancerDao", NetworkExternalLoadBalancerDaoImpl.class);
        addDao("NetworkExternalFirewallDao", NetworkExternalFirewallDaoImpl.class);
        addDao("ClusterVSMMapDao", ClusterVSMMapDaoImpl.class);
        addDao("PortProfileDao", PortProfileDaoImpl.class);
        addDao("PhysicalNetworkTrafficTypeDao", PhysicalNetworkTrafficTypeDaoImpl.class);
        addDao("NetworkServiceMapDao", NetworkServiceMapDaoImpl.class);
        addDao("StorageNetworkIpAddressDao", StorageNetworkIpAddressDaoImpl.class);
        addDao("StorageNetworkIpRangeDao", StorageNetworkIpRangeDaoImpl.class);
        addDao("VpcDao", VpcDaoImpl.class);
        addDao("VpcOfferingDao", VpcOfferingDaoImpl.class);
        addDao("VpcOfferingServiceMapDao", VpcOfferingServiceMapDaoImpl.class);
        addDao("PrivateIpDao", PrivateIpDaoImpl.class);
        addDao("VpcGatewayDao", VpcGatewayDaoImpl.class);
        addDao("StaticRouteDao", StaticRouteDaoImpl.class);
        addDao("TagsDao", ResourceTagsDaoImpl.class);
        addDao("Site2SiteVpnGatewayDao", Site2SiteVpnGatewayDaoImpl.class);
        addDao("Site2SiteCustomerGatewayDao", Site2SiteCustomerGatewayDaoImpl.class);
        addDao("Site2SiteVpnConnnectionDao", Site2SiteVpnConnectionDaoImpl.class);

        addDao("UserVmJoinDao", UserVmJoinDaoImpl.class);
        addDao("DomainRouterJoinDao", DomainRouterJoinDaoImpl.class);
        addDao("SecurityGroupJoinDao", SecurityGroupJoinDaoImpl.class);
        addDao("ResourceTagJoinDao", ResourceTagJoinDaoImpl.class);
        addDao("EventJoinDao", EventJoinDaoImpl.class);
        addDao("UserAccountJoinDao", UserAccountJoinDaoImpl.class);
        addDao("ProjectJoinDao", ProjectJoinDaoImpl.class);
        addDao("ProjectAccountJoinDao", ProjectAccountJoinDaoImpl.class);
        addDao("ProjectInvitationJoinDao", ProjectInvitationJoinDaoImpl.class);
        addDao("HostJoinDao", HostJoinDaoImpl.class);
        addDao("VolumeJoinDao", VolumeJoinDaoImpl.class);
        addDao("AccountJoinDao", AccountJoinDaoImpl.class);
        addDao("AsyncJobJoinDao", AsyncJobJoinDaoImpl.class);
        addDao("StoragePoolJoinDao", StoragePoolJoinDaoImpl.class);
        addDao("DiskOfferingJoinDao", DiskOfferingJoinDaoImpl.class);
        addDao("ServiceOfferingJoinDao", ServiceOfferingJoinDaoImpl.class);
        addDao("DataCenterJoinDao", DataCenterJoinDaoImpl.class);
    }

    @Override
    public synchronized Map<String, ComponentInfo<GenericDao<?, ?>>> getDaos() {
        if (_daos.size() == 0) {
            populateDaos();
        }
        //FIXME: Incorrect method return definition
        return _daos;
    }

    protected void populateManagers() {
        addManager("StackMaidManager", CheckPointManagerImpl.class);
        addManager("Cluster Manager", ClusterManagerImpl.class);
        addManager("ClusterFenceManager", ClusterFenceManagerImpl.class);
        addManager("ClusteredAgentManager", ClusteredAgentManagerImpl.class);
        addManager("SyncQueueManager", SyncQueueManagerImpl.class);
        addManager("AsyncJobManager", AsyncJobManagerImpl.class);
        addManager("AsyncJobExecutorContext", AsyncJobExecutorContextImpl.class);
        addManager("configuration manager", ConfigurationManagerImpl.class);
        addManager("account manager", AccountManagerImpl.class);
        addManager("domain manager", DomainManagerImpl.class);
        addManager("resource limit manager", ResourceLimitManagerImpl.class);
        addManager("network service", NetworkServiceImpl.class);
        addManager("network manager", NetworkManagerImpl.class);
        addManager("network model", NetworkModelImpl.class);
        addManager("download manager", DownloadMonitorImpl.class);
        addManager("upload manager", UploadMonitorImpl.class);
        addManager("keystore manager", KeystoreManagerImpl.class);
        addManager("secondary storage vm manager", SecondaryStorageManagerImpl.class);
        addManager("vm manager", UserVmManagerImpl.class);
        addManager("upgrade manager", UpgradeManagerImpl.class);
        addManager("StorageManager", StorageManagerImpl.class);
        addManager("Alert Manager", AlertManagerImpl.class);
        addManager("Template Manager", TemplateManagerImpl.class);
        addManager("Snapshot Manager", SnapshotManagerImpl.class);
        addManager("SnapshotScheduler", SnapshotSchedulerImpl.class);
        addManager("SecurityGroupManager", SecurityGroupManagerImpl2.class);
        addManager("EntityManager", EntityManagerImpl.class);
        addManager("LoadBalancingRulesManager", LoadBalancingRulesManagerImpl.class);
        addManager("AutoScaleManager", AutoScaleManagerImpl.class);
        addManager("RulesManager", RulesManagerImpl.class);
        addManager("RemoteAccessVpnManager", RemoteAccessVpnManagerImpl.class);
        addManager("Capacity Manager", CapacityManagerImpl.class);
        addManager("VirtualMachineManager", ClusteredVirtualMachineManagerImpl.class);
        addManager("HypervisorGuruManager", HypervisorGuruManagerImpl.class);
        addManager("ResourceManager", ResourceManagerImpl.class);
        addManager("IdentityManager", IdentityServiceImpl.class);
        addManager("OCFS2Manager", OCFS2ManagerImpl.class);
        addManager("FirewallManager", FirewallManagerImpl.class);
        ComponentInfo<? extends Manager> info = addManager("ConsoleProxyManager", ConsoleProxyManagerImpl.class);
        info.addParameter("consoleproxy.sslEnabled", "true");
        addManager("ProjectManager", ProjectManagerImpl.class);
        addManager("SwiftManager", SwiftManagerImpl.class);
        addManager("S3Manager", S3ManagerImpl.class);
        addManager("StorageNetworkManager", StorageNetworkManagerImpl.class);
        addManager("ExternalLoadBalancerUsageManager", ExternalLoadBalancerUsageManagerImpl.class);
        addManager("HA Manager", HighAvailabilityManagerImpl.class);
        addManager("VPC Manager", VpcManagerImpl.class);
        addManager("VpcVirtualRouterManager", VpcVirtualNetworkApplianceManagerImpl.class);
        addManager("NetworkACLManager", NetworkACLManagerImpl.class);
        addManager("TaggedResourcesManager", TaggedResourceManagerImpl.class);
        addManager("Site2SiteVpnManager", Site2SiteVpnManagerImpl.class);
        addManager("QueryManager", QueryManagerImpl.class);
    }

    @Override
    public synchronized Map<String, ComponentInfo<Manager>> getManagers() {
        if (_managers.size() == 0) {
            populateManagers();
        }
        return _managers;
    }

    protected void populateAdapters() {
        addAdapter(TemplateAdapter.class, TemplateAdapterType.Hypervisor.getName(), HyervisorTemplateAdapter.class);
    }

    @Override
    public synchronized Map<String, List<ComponentInfo<Adapter>>> getAdapters() {
        if (_adapters.size() == 0) {
            populateAdapters();
        }
        return _adapters;
    }

    @Override
    public synchronized Map<Class<?>, Class<?>> getFactories() {
        HashMap<Class<?>, Class<?>> factories = new HashMap<Class<?>, Class<?>>();
        factories.put(EntityManager.class, EntityManagerImpl.class);
        return factories;
    }

    protected void populateServices() {
        addService("VirtualRouterElementService", VirtualRouterElementService.class, VirtualRouterElement.class);
    }

    @Override
    public synchronized Map<String, ComponentInfo<PluggableService>> getPluggableServices() {
        if (_pluggableServices.size() == 0) {
            populateServices();
        }
        return _pluggableServices;
    }
}