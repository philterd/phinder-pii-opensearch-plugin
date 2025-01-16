#!/bin/bash -e

curl -s http://localhost:9200/sample_index/_search -H "Content-Type: application/json" -d'
                                                 {
                                                  "ext": {
                                                   "phinder": {
                                                      "field": "description",
                                                      "policy": "{\"identifiers\": {\"emailAddress\":{\"emailAddressFilterStrategies\":[{\"strategy\":\"REDACT\",\"redactionFormat\":\"{{{REDACTED-%t}}}\"}]}}}"
                                                    }
                                                   },
                                                   "query": {
                                                     "match_all": {}
                                                   }
                                                 }' | jq