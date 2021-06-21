# netflix-dgs-authorisation-directives

Sample application to demonstrate using custom GraphQL Schema directive to implement authorisation checks in a GraphQL API built using <a href="https://netflix.github.io/dgs/">Netflix DGS Framework</a>.

# Running and Testing
Start the application and navigate to http://localhost:6001/graphiql in browser.

Try below request without passing the USER-UUID header,
```
query{   
   getVideoForTopic(topic:"Physics"){    
      title    
      description   
      url   
      playbackToken   
   } 
}
```

Application should return below error:
```
{
  "errors": [
    {
      "message": "Exception while fetching data (/playbackToken): not authorized",
      "locations": [
        {
          "line": 10,
          "column": 5
        }
      ],
      "path": [
        "getVideoForTopic",
        "playbackToken"
      ],
      "extensions": {
        "errorType": "UNAUTHORIZED_ACCESS",
        "message": "errors.unauthorizedAccess",
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": {
    "getVideoForTopic": {
      "title": "The Map of Physics",
      "description": "The Map of Physics",
      "url": "https://www.youtube.com/watch?v=ZihywtixUYo",
      "playbackToken": null
    }
  }
}
```

Now add below header to request and try again
`"USER-UUID":"a18c0991-eb8f-319a-84bf-57d48cbd543c"`

The response should include a playback token.

**Coming Soon- A blog post that explains the design and implementation**
