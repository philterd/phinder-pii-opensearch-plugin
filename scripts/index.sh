#!/bin/bash -e

curl -s -X PUT "http://localhost:9200/sample_index" -H 'Content-Type: application/json' | jq

curl -s -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Another Example",
  "price": 19.99,
  "description": "My email is jeff.zemerick@philterd.ai ok!"
}' | jq
