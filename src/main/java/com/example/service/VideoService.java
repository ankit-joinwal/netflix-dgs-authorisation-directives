package com.example.service;

import com.example.types.Video;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsQuery;
import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.GraphqlErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DgsComponent
public class VideoService {

    private static Map<String,String> videoByTopic = Map.of("Physics","b1941275-75c1-41c2-82f3-b1e8c2d8a649");

    private static Map<String, Video> videoById = new HashMap<>();

    static{
        Video video = Video.newBuilder().topic("Physics")
                .url("https://www.youtube.com/watch?v=ZihywtixUYo")
                .description("The Map of Physics")
                .title("The Map of Physics")
                .uuid(UUID.fromString("b1941275-75c1-41c2-82f3-b1e8c2d8a649"))
                .build();
        videoById.put("b1941275-75c1-41c2-82f3-b1e8c2d8a649",video);

    }

    @DgsQuery
    public Video getVideoForTopic(String topic) {
        String videoId = videoByTopic.get(topic);
        if(videoId == null){
            throw new GraphQLException("Video not found");
        }
        return videoById.get(videoId);

    }

    @DgsData(parentType = "Video")
    public String playbackToken(){
        return "U2FtcGxlIFRva2Vu";
    }

}
