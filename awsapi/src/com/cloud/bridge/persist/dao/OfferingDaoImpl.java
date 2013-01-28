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
package com.cloud.bridge.persist.dao;


import javax.ejb.Local;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.cloud.bridge.model.OfferingBundleVO;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;

@Local(value={OfferingDao.class})
public class OfferingDaoImpl extends GenericDaoBase<OfferingBundleVO, Long> implements OfferingDao {
	public static final Logger logger = Logger.getLogger(OfferingDaoImpl.class);

	public OfferingDaoImpl() {}
	
	@Override
	public int getOfferingCount() {
       Transaction txn = Transaction.open(Transaction.AWSAPI_DB);
        try {
            txn.start();
            return listAll().size();
        }finally {
            txn.close();
        }
	    
	}
	
	@Override
	public String getCloudOffering( String amazonEC2Offering ) {
	    
       SearchBuilder <OfferingBundleVO> searchByAmazon = createSearchBuilder();
       searchByAmazon.and("AmazonEC2Offering", searchByAmazon.entity().getAmazonOffering() , SearchCriteria.Op.EQ);
       searchByAmazon.done();
       Transaction txn = Transaction.open(Transaction.AWSAPI_DB);
       try {
           txn.start();
           SearchCriteria<OfferingBundleVO> sc = searchByAmazon.create();
           sc.setParameters("AmazonEC2Offering", amazonEC2Offering);
           return findOneBy(sc).getCloudstackOffering();
           
        } finally {
            txn.close();
        }
	}
	
	@Override
	public String getAmazonOffering( String cloudStackOffering ) {
        
	       SearchBuilder <OfferingBundleVO> searchByAmazon = createSearchBuilder();
	       searchByAmazon.and("CloudStackOffering", searchByAmazon.entity().getAmazonOffering() , SearchCriteria.Op.EQ);
	       searchByAmazon.done();
	       Transaction txn = Transaction.open(Transaction.AWSAPI_DB);
	       try {
	           txn.start();
	           SearchCriteria<OfferingBundleVO> sc = searchByAmazon.create();
	           sc.setParameters("CloudStackOffering", cloudStackOffering);
	           return findOneBy(sc).getAmazonOffering();
	           
	        } finally {
	            txn.close();
	        }
	    }
	
	@Override
	public void setOfferMapping( String amazonEC2Offering, String cloudStackOffering ) {
	    
        SearchBuilder <OfferingBundleVO> searchByAmazon = createSearchBuilder();
        searchByAmazon.and("CloudStackOffering", searchByAmazon.entity().getAmazonOffering() , SearchCriteria.Op.EQ);
        searchByAmazon.and("AmazonEC2Offering", searchByAmazon.entity().getCloudstackOffering() , SearchCriteria.Op.EQ);
        searchByAmazon.done();
        Transaction txn = Transaction.open(Transaction.AWSAPI_DB);
        OfferingBundleVO offering = null;
        try {
            txn.start();
            SearchCriteria<OfferingBundleVO> sc = searchByAmazon.create();
            sc.setParameters("CloudStackOffering", cloudStackOffering);
            sc.setParameters("AmazonEC2Offering", amazonEC2Offering);
            offering = findOneBy(sc);
            if (null == offering) {
                offering = new OfferingBundleVO();
            }
            offering.setAmazonOffering(amazonEC2Offering);
            offering.setCloudstackOffering(cloudStackOffering);
            if (null == offering) 
                offering = persist(offering);
            else
                update(offering.getID(), offering);

            txn.commit();
         } finally {
             txn.close();
         }

	}
	
	@Override
	public void deleteOfferMapping( String amazonEC2Offering ) {
	    SearchBuilder <OfferingBundleVO> searchByAmazon = createSearchBuilder();
        searchByAmazon.and("AmazonEC2Offering", searchByAmazon.entity().getAmazonOffering() , SearchCriteria.Op.EQ);
        searchByAmazon.done();
        Transaction txn = Transaction.open(Transaction.AWSAPI_DB);
        try {
            txn.start();
            SearchCriteria<OfferingBundleVO> sc = searchByAmazon.create();
            sc.setParameters("AmazonEC2Offering", amazonEC2Offering);
            remove(sc);
            txn.commit();
         } finally {
             txn.close();
         }
	}
	
}
