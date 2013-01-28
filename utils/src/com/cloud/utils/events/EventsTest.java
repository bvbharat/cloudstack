// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.utils.events;

public class EventsTest {
	public void onWeatherChange(Object sender, EventArgs args) {
		System.out.println("onWeatherChange, weather: " + ((WeatherChangeEventArgs)args).getWeather());
	}
	
	public void onTrafficChange(Object sender, EventArgs args) {
		System.out.println("onTrafficChange");
	}
	
	public void run() {
		SubscriptionMgr mgr = SubscriptionMgr.getInstance();
		try {
			mgr.subscribe("weather", this, "onWeatherChange");
			mgr.subscribe("traffic", this, "onTrafficChange");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		mgr.notifySubscribers("weather", null, new WeatherChangeEventArgs("weather", "Sunny"));
		mgr.notifySubscribers("traffic", null, EventArgs.Empty);
	}

	public static void main(String[] args) {
		EventsTest test = new EventsTest();
		test.run();
	}
}

class WeatherChangeEventArgs extends EventArgs {
	private static final long serialVersionUID = -952166331523609047L;
	
	private String weather;
	
	public WeatherChangeEventArgs() {
	}
	
	public WeatherChangeEventArgs(String subject, String weather) {
		super(subject);
		this.weather = weather;
	}
	
	public String getWeather() { return weather; }
	public void setWeather(String weather) {
		this.weather = weather;
	}
}
