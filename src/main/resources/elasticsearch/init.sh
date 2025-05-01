curl -u elastic:Qwe54188_ -X DELETE "http://localhost:9200/question_v1"

curl -u elastic:Qwe54188_ -X PUT "http://127.0.0.1:9200/question_v1" \
  -H 'Content-Type: application/json' \
  -d '{
        "aliases": {
          "question": {}
        },
        "mappings": {
          "properties": {
            "title": {
              "type": "text",
              "analyzer": "ik_max_word",
              "search_analyzer": "ik_smart",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            },
            "content": {
              "type": "text",
              "analyzer": "ik_max_word",
              "search_analyzer": "ik_smart"
            },
            "tags": {
              "type": "keyword"
            },
            "answer": {
              "type": "text",
              "analyzer": "ik_max_word",
              "search_analyzer": "ik_smart"
            },
            "userId": {
              "type": "long"
            },
            "editTime": {
              "type": "date",
              "format": "yyyy-MM-dd HH:mm:ss"
            },
            "createTime": {
              "type": "date",
              "format": "yyyy-MM-dd HH:mm:ss"
            },
            "updateTime": {
              "type": "date",
              "format": "yyyy-MM-dd HH:mm:ss"
            },
            "isDelete": {
              "type": "keyword"
            }
          }
        }
      }'

curl -u elastic:Qwe54188_ -X GET "http://127.0.0.1:9200/question_v1/_mapping?pretty"
