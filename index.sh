#!/bin/bash -e

curl -X PUT "http://localhost:9200/sample_index" -H 'Content-Type: application/json'

curl -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Another Example",
  "price": 19.99,
  "description": "We are such stuff as dreams are made on"
}'
