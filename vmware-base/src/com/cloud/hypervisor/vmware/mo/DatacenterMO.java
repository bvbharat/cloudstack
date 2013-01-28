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

package com.cloud.hypervisor.vmware.mo;

import java.util.ArrayList;
import java.util.List;

import com.cloud.hypervisor.vmware.util.VmwareContext;
import com.cloud.utils.Pair;
import com.vmware.apputils.vim25.ServiceUtil;
import com.vmware.vim25.CustomFieldStringValue;
import com.vmware.vim25.DVPortgroupConfigInfo;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;

public class DatacenterMO extends BaseMO {
	
	public DatacenterMO(VmwareContext context, ManagedObjectReference morDc) {
		super(context, morDc);
	}
	
	public DatacenterMO(VmwareContext context, String morType, String morValue) {
		super(context, morType, morValue);
	}
	
	public DatacenterMO(VmwareContext context, String dcName) throws Exception {
		super(context, null);
		
		_mor = _context.getServiceUtil().getDecendentMoRef(_context.getRootFolder(), "Datacenter", dcName);
		assert(_mor != null);
	}
	
	public String getName() throws Exception {
		return (String)_context.getServiceUtil().getDynamicProperty(_mor, "name");
	}
	
	public void registerTemplate(ManagedObjectReference morHost, String datastoreName, 
		String templateName, String templateFileName) throws Exception {
		
		ServiceUtil serviceUtil = _context.getServiceUtil();
		
		ManagedObjectReference morFolder = (ManagedObjectReference)serviceUtil.getDynamicProperty(
			_mor, "vmFolder");
		assert(morFolder != null);
		
		ManagedObjectReference morTask = _context.getService().registerVM_Task(
    		 morFolder, 
    		 String.format("[%s] %s/%s", datastoreName, templateName, templateFileName),
    		 templateName, true, 
    		 null, morHost);
		
		String result = serviceUtil.waitForTask(morTask);
		if (!result.equalsIgnoreCase("Sucess")) {
			throw new Exception("Unable to register template due to " + TaskMO.getTaskFailureInfo(_context, morTask));
		} else {
			_context.waitForTaskProgressDone(morTask);
		}
	}
	
	public VirtualMachineMO findVm(String vmName) throws Exception {
		ObjectContent[] ocs = getVmPropertiesOnDatacenterVmFolder(new String[] { "name" });
		if(ocs != null && ocs.length > 0) {
			for(ObjectContent oc : ocs) {
				DynamicProperty[] props = oc.getPropSet();
				if(props != null) {
					for(DynamicProperty prop : props) {
						if(prop.getVal().toString().equals(vmName))
							return new VirtualMachineMO(_context, oc.getObj());
					}
				}
			}
		}
		return null;
	}
	
	public List<VirtualMachineMO> findVmByNameAndLabel(String vmLabel) throws Exception {
		CustomFieldsManagerMO cfmMo = new CustomFieldsManagerMO(_context, 
				_context.getServiceContent().getCustomFieldsManager());
		int key = cfmMo.getCustomFieldKey("VirtualMachine", CustomFieldConstants.CLOUD_UUID);
		assert(key != 0);
		
		List<VirtualMachineMO> list = new ArrayList<VirtualMachineMO>();
		
		ObjectContent[] ocs = getVmPropertiesOnDatacenterVmFolder(new String[] { "name",  String.format("value[%d]", key)});
		if(ocs != null && ocs.length > 0) {
			for(ObjectContent oc : ocs) {
				DynamicProperty[] props = oc.getPropSet();
				if(props != null) {
					for(DynamicProperty prop : props) {
						if(prop.getVal() != null) {
							if(prop.getName().equalsIgnoreCase("name")) {
								if(prop.getVal().toString().equals(vmLabel)) {
									list.add(new VirtualMachineMO(_context, oc.getObj()));
									break;		// break out inner loop
								}
							} else if(prop.getVal() instanceof CustomFieldStringValue) {
								String val = ((CustomFieldStringValue)prop.getVal()).getValue();
								if(val.equals(vmLabel)) {
									list.add(new VirtualMachineMO(_context, oc.getObj()));
									break;		// break out inner loop
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	public List<Pair<ManagedObjectReference, String>> getAllVmsOnDatacenter() throws Exception {
	    List<Pair<ManagedObjectReference, String>> vms = new ArrayList<Pair<ManagedObjectReference, String>>();
	    
	    ObjectContent[] ocs = getVmPropertiesOnDatacenterVmFolder(new String[] { "name" });
	    if(ocs != null) {
	        for(ObjectContent oc : ocs) {
	            String vmName = oc.getPropSet(0).getVal().toString();
	            vms.add(new Pair<ManagedObjectReference, String>(oc.getObj(), vmName));
	        }
	    }
	    
	    return vms;
	}	
	
	public ManagedObjectReference findDatastore(String name) throws Exception {
		assert(name != null);
		
		ObjectContent[] ocs = getDatastorePropertiesOnDatacenter(new String[] { "name" });
		if(ocs != null) {
			for(ObjectContent oc : ocs) {
				if(oc.getPropSet(0).getVal().toString().equals(name)) {
					return oc.getObj();
				}
			}
		}
		return null;
	}
	
	public ManagedObjectReference findHost(String name) throws Exception {
		ObjectContent[] ocs= getHostPropertiesOnDatacenterHostFolder(new String[] { "name" });
		
		if(ocs != null) {
			for(ObjectContent oc : ocs) {
				if(oc.getPropSet(0).getVal().toString().equals(name)) {
					return oc.getObj();
				}
			}
		}
		return null;
	}
	
	public ManagedObjectReference getVmFolder() throws Exception {
		return (ManagedObjectReference)_context.getServiceUtil().getDynamicProperty(_mor, "vmFolder");
	}
	
	public ObjectContent[] getHostPropertiesOnDatacenterHostFolder(String[] propertyPaths) throws Exception {
		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("HostSystem");
		pSpec.setPathSet(propertyPaths);
		
	    TraversalSpec computeResource2HostTraversal = new TraversalSpec();
	    computeResource2HostTraversal.setType("ComputeResource");
	    computeResource2HostTraversal.setPath("host");
	    computeResource2HostTraversal.setName("computeResource2HostTraversal");
		
	    SelectionSpec recurseFolders = new SelectionSpec();
	    recurseFolders.setName("folder2childEntity");
	      
	    TraversalSpec folder2childEntity = new TraversalSpec();
	    folder2childEntity.setType("Folder");
	    folder2childEntity.setPath("childEntity");
	    folder2childEntity.setName(recurseFolders.getName());
	    folder2childEntity.setSelectSet(new SelectionSpec[] { recurseFolders, computeResource2HostTraversal });
	    
	    TraversalSpec dc2HostFolderTraversal = new TraversalSpec();
	    dc2HostFolderTraversal.setType("Datacenter");
	    dc2HostFolderTraversal.setPath("hostFolder");
	    dc2HostFolderTraversal.setName("dc2HostFolderTraversal");
	    dc2HostFolderTraversal.setSelectSet(new SelectionSpec[] { folder2childEntity } );
	    
	    ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(_mor);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { dc2HostFolderTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    return _context.getService().retrieveProperties(
	    	_context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec }); 
	}
	
	public ObjectContent[] getDatastorePropertiesOnDatacenter(String[] propertyPaths) throws Exception {
		
		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("Datastore");
		pSpec.setPathSet(propertyPaths);
		
	    TraversalSpec dc2DatastoreTraversal = new TraversalSpec();
	    dc2DatastoreTraversal.setType("Datacenter");
	    dc2DatastoreTraversal.setPath("datastore");
	    dc2DatastoreTraversal.setName("dc2DatastoreTraversal");
	    
	    ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(_mor);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { dc2DatastoreTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    return _context.getService().retrieveProperties(
	    	_context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec }); 
	}
	
	public ObjectContent[] getVmPropertiesOnDatacenterVmFolder(String[] propertyPaths) throws Exception {
		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("VirtualMachine");
		pSpec.setPathSet(propertyPaths);
		
	    TraversalSpec dc2VmFolderTraversal = new TraversalSpec();
	    dc2VmFolderTraversal.setType("Datacenter");
	    dc2VmFolderTraversal.setPath("vmFolder");
	    dc2VmFolderTraversal.setName("dc2VmFolderTraversal");
	    
	    SelectionSpec recurseFolders = new SelectionSpec();
	    recurseFolders.setName("folder2childEntity");
	      
	    TraversalSpec folder2childEntity = new TraversalSpec();
	    folder2childEntity.setType("Folder");
	    folder2childEntity.setPath("childEntity");
	    folder2childEntity.setName(recurseFolders.getName());
	    folder2childEntity.setSelectSet(new SelectionSpec[] { recurseFolders });
	    dc2VmFolderTraversal.setSelectSet(new SelectionSpec[] { folder2childEntity } );

	    ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(_mor);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { dc2VmFolderTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    return _context.getService().retrieveProperties(
	    	_context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec }); 
	}
	
	public static Pair<DatacenterMO, String> getOwnerDatacenter(VmwareContext context, 
		ManagedObjectReference morEntity) throws Exception {
		
		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("Datacenter");
		pSpec.setPathSet(new String[] { "name" });
		
	    TraversalSpec entityParentTraversal = new TraversalSpec();
	    entityParentTraversal.setType("ManagedEntity");
	    entityParentTraversal.setPath("parent");
	    entityParentTraversal.setName("entityParentTraversal");
	    entityParentTraversal.setSelectSet(new SelectionSpec[] { new SelectionSpec(null, null, "entityParentTraversal") });

	    ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(morEntity);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { entityParentTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    ObjectContent[] ocs = context.getService().retrieveProperties(
	    	context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec });
	    
	    assert(ocs != null);
	    assert(ocs[0].getObj() != null);
	    assert(ocs[0].getPropSet(0) != null);
	    assert(ocs[0].getPropSet(0).getVal() != null);
	    
	    String dcName = ocs[0].getPropSet(0).getVal().toString(); 
	    return new Pair<DatacenterMO, String>(new DatacenterMO(context, ocs[0].getObj()), dcName); 
	}
	

	public ManagedObjectReference getDvPortGroupMor(String dvPortGroupName) throws Exception {
    		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("DistributedVirtualPortgroup");
		pSpec.setPathSet(new String[] {"name"});
		
		TraversalSpec datacenter2DvPortGroupTraversal = new TraversalSpec();
		datacenter2DvPortGroupTraversal.setType("Datacenter");
		datacenter2DvPortGroupTraversal.setPath("network");
		datacenter2DvPortGroupTraversal.setName("datacenter2DvPortgroupTraversal");
		
		ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(_mor);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { datacenter2DvPortGroupTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    ObjectContent[] ocs = _context.getService().retrieveProperties(
	    	_context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec });
	    
	    if(ocs != null) {
	    	for(ObjectContent oc : ocs) {
	    		DynamicProperty[] props = oc.getPropSet();
	    		if(props != null) {
	    			for(DynamicProperty prop : props) {
	    				if(prop.getVal().equals(dvPortGroupName))
	    					return oc.getObj();
	    			}
	    		}
	    	}
	    }
	    return null;
	}	

	public boolean hasDvPortGroup(String dvPortGroupName) throws Exception {
		ManagedObjectReference morNetwork = getDvPortGroupMor(dvPortGroupName);
		if(morNetwork != null)
			return true;
		return false;		
	}
	
	public DVPortgroupConfigInfo getDvPortGroupSpec(String dvPortGroupName) throws Exception {
		DVPortgroupConfigInfo configSpec = null;
		String nameProperty = null;
		PropertySpec pSpec = new PropertySpec();
		pSpec.setType("DistributedVirtualPortgroup");
		pSpec.setPathSet(new String[] {"name", "config"});
		
	    TraversalSpec datacenter2DvPortGroupTraversal = new TraversalSpec();
	    datacenter2DvPortGroupTraversal.setType("Datacenter");
	    datacenter2DvPortGroupTraversal.setPath("network");
	    datacenter2DvPortGroupTraversal.setName("datacenter2DvPortgroupTraversal");

	    ObjectSpec oSpec = new ObjectSpec();
	    oSpec.setObj(_mor);
	    oSpec.setSkip(Boolean.TRUE);
	    oSpec.setSelectSet(new SelectionSpec[] { datacenter2DvPortGroupTraversal });

	    PropertyFilterSpec pfSpec = new PropertyFilterSpec();
	    pfSpec.setPropSet(new PropertySpec[] { pSpec });
	    pfSpec.setObjectSet(new ObjectSpec[] { oSpec });
	    
	    ObjectContent[] ocs = _context.getService().retrieveProperties(
	    	_context.getServiceContent().getPropertyCollector(), 
	    	new PropertyFilterSpec[] { pfSpec });
	    
	    if(ocs != null) {
	    	for(ObjectContent oc : ocs) {
	    		DynamicProperty[] props = oc.getPropSet();
	    		if(props != null) {
	    			assert(props.length == 2);
	    			for(DynamicProperty prop : props) {
	    				if(prop.getName().equals("config")) {
	    					  configSpec = (DVPortgroupConfigInfo) prop.getVal();
	    				}
	    				else {
	    					nameProperty = prop.getVal().toString();
	    				}
	    			}
	    			if(nameProperty.equalsIgnoreCase(dvPortGroupName)) {
	    				return configSpec;	    			
	    			}
	    		}
	    	}
	    }
	    return null;
	}

    public ManagedObjectReference getDvSwitchMor(ManagedObjectReference dvPortGroupMor) throws Exception {
        String dvPortGroupKey = null;
        ManagedObjectReference dvSwitchMor = null;
        PropertySpec pSpec = new PropertySpec();
        pSpec.setType("DistributedVirtualPortgroup");
        pSpec.setPathSet(new String[] { "key", "config.distributedVirtualSwitch" });

        TraversalSpec datacenter2DvPortGroupTraversal = new TraversalSpec();
        datacenter2DvPortGroupTraversal.setType("Datacenter");
        datacenter2DvPortGroupTraversal.setPath("network");
        datacenter2DvPortGroupTraversal.setName("datacenter2DvPortgroupTraversal");

        ObjectSpec oSpec = new ObjectSpec();
        oSpec.setObj(_mor);
        oSpec.setSkip(Boolean.TRUE);
        oSpec.setSelectSet(new SelectionSpec[] { datacenter2DvPortGroupTraversal });

        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        pfSpec.setPropSet(new PropertySpec[] { pSpec });
        pfSpec.setObjectSet(new ObjectSpec[] { oSpec });

        ObjectContent[] ocs = _context.getService().retrieveProperties(
                _context.getServiceContent().getPropertyCollector(),
                new PropertyFilterSpec[] { pfSpec });

        if (ocs != null) {
            for (ObjectContent oc : ocs) {
                DynamicProperty[] props = oc.getPropSet();
                if (props != null) {
                    assert (props.length == 2);
                    for (DynamicProperty prop : props) {
                        if (prop.getName().equals("key")) {
                            dvPortGroupKey = (String) prop.getVal();
                        }
                        else {
                            dvSwitchMor = (ManagedObjectReference) prop.getVal();
                        }
                    }
                    if ((dvPortGroupKey != null) && dvPortGroupKey.equals(dvPortGroupMor.get_value())) {
                        return dvSwitchMor;
                    }
                }
            }
        }
        return null;
    }

    public String getDvSwitchUuid(ManagedObjectReference dvSwitchMor) throws Exception {
        assert (dvSwitchMor != null);
        return (String) _context.getServiceUtil().getDynamicProperty(dvSwitchMor, "uuid");
    }

    public VirtualEthernetCardDistributedVirtualPortBackingInfo getDvPortBackingInfo(Pair<ManagedObjectReference, String> networkInfo)
            throws Exception {
        assert (networkInfo != null);
        assert (networkInfo.first() != null && networkInfo.first().getType().equalsIgnoreCase("DistributedVirtualPortgroup"));
        final VirtualEthernetCardDistributedVirtualPortBackingInfo dvPortBacking = new VirtualEthernetCardDistributedVirtualPortBackingInfo();
        final DistributedVirtualSwitchPortConnection dvPortConnection = new DistributedVirtualSwitchPortConnection();
        ManagedObjectReference dvsMor = getDvSwitchMor(networkInfo.first());
        String dvSwitchUuid = getDvSwitchUuid(dvsMor);
        dvPortConnection.setSwitchUuid(dvSwitchUuid);
        dvPortConnection.setPortgroupKey(networkInfo.first().get_value());
        dvPortBacking.setPort(dvPortConnection);
        System.out.println("Plugging NIC device into network " + networkInfo.second() + " backed by dvSwitch: "
                + dvSwitchUuid);
        return dvPortBacking;
    }
}
