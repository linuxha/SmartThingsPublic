/**
 *  Virtual Dimmer
 *
 *  Copyright 2015 Michael Struck
 *  Version 1.1.0 1/3/16
 *
 *  Version 1.0.0 - Initial release
 *  Version 1.1.0 - Updated the interface to better match SmartThings dimmers (thanks to @BoruGee)
 * 
 *  Uses code from SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
        definition (name: "vDimmer", namespace: "linuxha", author: "SmartThings") {
        capability "Switch"
        capability "Refresh"
        capability "Switch Level"
    }

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true, canChangeBackground: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
    			attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
		      	attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
		      	attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "turningOff"
        	}
        		tileAttribute("device.level", key: "SLIDER_CONTROL") {
            		attributeState "level", action:"switch level.setLevel"
        		}
        		tileAttribute("level", key: "SECONDARY_CONTROL") {
              		attributeState "level", label: 'Light dimmed to ${currentValue}%'
        		}    
		}
        valueTile("lValue", "device.level", inactiveLabel: true, height:2, width:2, decoration: "flat") {  
			state "levelValue", label:'${currentValue}%', unit:"", backgroundColor: "#53a7c0"  
        }  
    
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main "switch"
		details(["switch","on","off","level","lValue","refresh"])

	}
}

def parse(String description) {
}

def on() {
	sendEvent(name: "switch", value: "on")
    log.info "Dimmer On"
}

def off() {
	sendEvent(name: "switch", value: "off")
    log.info "Dimmer Off"
}

def setLevel(val){
    log.info "setLevel $val"
    def tval = val.toInteger()
    // make sure we don't drive switches past allowed values (command will hang device waiting for it to
    // execute. Never commes back)
    if (tval < 0){
    	tval = 0
    }
    
    if( tval > 100){
    	tval = 100
    }
    
    if (tval == 0){ 
    	sendEvent(name:"level",value:tval)
    	off()
        log.info "OFF: setLevel $tval"
    } else {
    	on()
    	sendEvent(name:"level",value:tval)
    	sendEvent(name:"switch.setLevel",value:tval)
        log.info "ON:s github.com/lientLevel $tval"
    }
}

def refresh() {
    log.info "refresh"
}