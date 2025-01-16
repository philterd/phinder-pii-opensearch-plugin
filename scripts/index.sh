#!/bin/bash -e

curl -s -X DELETE "http://localhost:9200/sample_index" | jq

curl -s -X PUT "http://localhost:9200/sample_index" -H 'Content-Type: application/json' | jq

curl -s -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Another Example",
  "description": "My email is something@something.com ok!"
}' | jq

curl -s -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Yet Another Example",
  "description": "No email addresses in this one"
}' | jq

curl -s -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "A Third Example",
  "description": "tom@tom.com"
}' | jq
