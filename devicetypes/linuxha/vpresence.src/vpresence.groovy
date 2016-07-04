/*
 *  Copyright 2016 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy 
 *  of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 *  License for the specific language governing permissions and limitations 
 *  under the License.
 */
 
metadata {
    definition (name: "vPresence", namespace: "linuxha", author: "SmartThings") {
        capability "Actuator"
        capability "Presence Sensor"
        capability "Sensor"

        command "left"
        command "arrived"
    }

    simulator {
        status "present":     "presence: 1"
        status "not present": "presence: 0"
    }

    tiles {
        /* */
        standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
            state("present",     labelIcon:"st.presence.tile.mobile-present",     backgroundColor:"#53a7c0")
            state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ebeef2")
        }
        /* * /
        standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
            state("present",     action='presence.arrived', labelIcon:"st.presence.tile.mobile-present",     backgroundColor:"#53a7c0")
            state("not present", action='presence.left',    labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ebeef2")
        }
        */
        standardTile("Present", "device.presence", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"presence.arrived", icon:"st.secondary.strobe", backgroundColor:"#cccccc"
        }
        standardTile("Away", "device.presence", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"presence.left", icon:"st.secondary.siren", backgroundColor:"#cccccc"
        }
                
        main "presence"
        details( [ "Present", "Away" ] )
        /* */
        /* * /
        main    "presence"
        details "presence"
        /* */
    }
}

/*
def installed() {
    log.debug "presence installed"
    subscribe(presence, "presence", parse)
}
*/
def parse(String description) {
    def name            = parseName(description)
    def value           = parseValue(description)
    def linkText        = getLinkText(device)
    def descriptionText = parseDescriptionText(linkText, value, description)
    def handlerName     = getState(value)
    def isStateChange   = isStateChange(device, name, value)

    def results = [
        translatable:    true,
        name:            name,
        value:           value,
        unit:            null,
        linkText:        linkText,
        descriptionText: descriptionText,
        handlerName:     handlerName,
        isStateChange:   isStateChange,
        displayed:       displayed(description, isStateChange)
    ]
    log.debug "Parse returned $results.descriptionText"

    //def pair = description.split(":")
    //createEvent(name: pair[0].trim(), value: pair[1].trim())

    log.debug "vPresence: After createEvent"
    return results
}

private String parseName(String description) {
    if (description?.startsWith("presence: ")) {
        return "presence"
    }
    null
}

private String parseValue(String description) {
    switch(description) {
        case "presence: 1": return "present"
        case "presence: 0": return "not present"
        default: return description
    }
}

private parseDescriptionText(String linkText, String value, String description) {
    switch(value) {
        case "present":
            return "{{ linkText }} has arrived"

        case "not present":
            return "{{ linkText }} has left"

        default:
            return value
    }
}

private getState(String value) {
    switch(value) {
        case "present":
            return "arrived"

        case "not present":
            return "left"

        default:
            return value
    }
}

def arrived() {
    log.debug "presence: arrived"
    sendEvent(name: "presence", value: "present")
}

def left() {
    log.debug "presence: left"

    sendEvent(name: "presence", value: "not present")
}