Swift repository plugin for Elasticsearch
=========================================

In order to install the plugin, simply run: `bin/plugin -install com.github.demon/elasticsearch-repository-swift/1.1.0`.

|      Swift Plugin           | elasticsearch         | Release date |
|-----------------------------|-----------------------|:------------:|
| 1.0-SNAPSHOT (master)       | 1.1.0                 |              |

## Create Repository
```
    $ curl -XPUT 'http://localhost:9200/_snapshot/my_backup' -d '{
        "type": "swift",
        "settings": {
            "swift_url": "http://localhost:8080/auth/v1.0/",
            "swift_container": "my-container",
            "swift_username": "myuser",
            "swift_password": "mypass!"
        }
    }'
```

See [Snapshot And Restore](http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/modules-snapshots.html) for more information


## Settings
|  Setting                            |   Description
|-------------------------------------|------------------------------------------------------------
| swift_url                          | Swift auth url. **Mandatory**
| swift_username                     | Swift username
| swift_password                     | Swift password
| concurrent_streams                 | Throttles the number of streams (per node) preforming snapshot operation. Defaults to `5`
| compress                           | Turns on compression of the snapshot files. Defaults to `true`.
| max_restore_bytes_per_sec          | Throttles per node restore rate. Defaults to `20mb` per second.
| max_snapshot_bytes_per_sec         | Throttles per node snapshot rate. Defaults to `20mb` per second.


## To debug in Eclipse
Since Swift has loggin dependencies you have to be careful about debugging in Eclipse.

1.  Import this project into Eclipse using the maven connector.  Do no import the main Elasticsearch code.
2.  Create a new java application debug configuration and set it to run ElasticsearchF.
3.  Go to the Classpath tab
4.  Click on Maven Dependiences
5.  Click on Advanced
6.  Click Add Folder
7.  Click ok
8.  Expand the tree to find <project-name>/src/test/resources
9.  Click ok
10. Click debug
