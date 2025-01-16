# Phinder PII Plugin for OpenSearch

This repository is a plugin for Amazon OpenSearch that redacts PII from search results. It uses the [phileas](https://github.com/philterd/phileas/) library for redaction.

## Usage

To use the plugin, create an index and then index some documents:

```
curl -s -X PUT "http://localhost:9200/sample_index" -H 'Content-Type: application/json' | jq
```

```
curl -s -X POST "http://localhost:9200/sample_index/_doc" -H 'Content-Type: application/json' -d'
    {
    "name": "Another Example",
    "price": 19.99,
    "description": "My email is something@something.com ok!"
    }' | jq
```

Next, do a search providing a filter policy and specifying which field you want to redact. In this example,
we are going to redact email addresses that appear in the `description` field:

```
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
```

In the response, you will see the email address in the indexed document has been redacted:

```
{
  "took": 2,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 1,
    "hits": [
      {
        "_index": "sample_index",
        "_id": "kK0rcJQB8PH88cC0FmJH",
        "_score": 1,
        "_source": {
          "name": "Another Example",
          "description": "My email is {{{REDACTED-email-address}}} ok!",
          "price": 19.99
        }
      }
    ]
  }
}
```

## License
This code is licensed under the Apache 2.0 License. See [LICENSE.txt](LICENSE.txt).

## Copyright
Copyright 2025 Philterd, LLC. See [NOTICE](NOTICE.txt) for details.
