package nu.studer.teamcity.buildscan.internal.slack

import com.google.common.collect.ImmutableMap
import nu.studer.teamcity.buildscan.BuildScanReference
import nu.studer.teamcity.buildscan.BuildScanReferences
import spock.lang.Specification

class SlackPayloadFactoryTest extends Specification {

    def factory = SlackPayloadFactory.create()

    def "properly converts single build scan to payload"() {
        given:
        def buildScanReferences = BuildScanReferences.of(new BuildScanReference("myId", "http://www.myUrl.org/s/abcde"))
        def params = [
            "system.teamcity.buildConfName": "My Configuration",
            "teamcity.serverUrl"           : "http://tc.server.org",
            "teamcity.build.id"            : "23"
        ]

        when:
        def payload = factory.from(buildScanReferences, ImmutableMap.of(), params)
        def json = SlackPayloadSerializer.create().toJson(payload)

        then:
        json == """{
  "text": "TeamCity <http://tc.server.org/viewLog.html?buildId=23|[My Configuration]> 1 build scan published:",
  "attachments": [
    {
      "color": "#000000",
      "pretext": "Build scan http://www.myUrl.org/s/abcde",
      "fallback": "Build scan http://www.myUrl.org/s/abcde",
      "mrkdwn_in": [
        "text",
        "pretext",
        "fields"
      ],
      "fields": []
    }
  ]
}"""
    }

    def "properly converts multiple build scans to payload"() {
        given:
        def buildScanReferences = BuildScanReferences.of([
            new BuildScanReference("myId", "http://www.myUrl.org/s/abcde"),
            new BuildScanReference("myOtherId", "http://www.myOtherUrl.org/efghi")
        ])
        def params = [
            "system.teamcity.buildConfName": "My Configuration",
            "teamcity.serverUrl"           : "http://tc.server.org",
            "teamcity.build.id"            : "23"
        ]
        when:
        def payload = factory.from(buildScanReferences, ImmutableMap.of(), params)
        def json = SlackPayloadSerializer.create().toJson(payload)

        then:
        json == """{
  "text": "TeamCity <http://tc.server.org/viewLog.html?buildId=23|[My Configuration]> 2 build scans published:",
  "attachments": [
    {
      "color": "#000000",
      "pretext": "Build scan http://www.myUrl.org/s/abcde",
      "fallback": "Build scan http://www.myUrl.org/s/abcde",
      "mrkdwn_in": [
        "text",
        "pretext",
        "fields"
      ],
      "fields": []
    },
    {
      "color": "#000000",
      "pretext": "Build scan http://www.myOtherUrl.org/efghi",
      "fallback": "Build scan http://www.myOtherUrl.org/efghi",
      "mrkdwn_in": [
        "text",
        "pretext",
        "fields"
      ],
      "fields": []
    }
  ]
}"""
    }

}