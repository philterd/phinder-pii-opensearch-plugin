#!/bin/bash -e

curl http://localhost:9200/sample_index/_search -H "Content-Type: application/json" -d'
                                                 {
                                                  "ext": {
                                                   "phinder": {
                                                      "field": "description"
                                                    }
                                                   },
                                                   "query": {
                                                     "match_all": {}
                                                   }
                                                 }' | jq