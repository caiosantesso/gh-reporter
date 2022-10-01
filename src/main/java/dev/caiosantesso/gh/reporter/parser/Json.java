package dev.caiosantesso.gh.reporter.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.caiosantesso.gh.reporter.api.BranchesEndpoint;
import dev.caiosantesso.gh.reporter.api.CodeSearchEndpoint;
import dev.caiosantesso.gh.reporter.api.ReposEndpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Json {
    private static final ObjectMapper objMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Json() {}

    private static class JsonParserException extends RuntimeException {
        public JsonParserException(IOException e) {
            super(e);
        }
    }

    public static List<ReposEndpoint.Repo> parseRepo(InputStream is) {
        var typeRef = new TypeReference<List<ReposEndpoint.Repo>>() {};
        try {
            return objMapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }

    public static List<BranchesEndpoint.Branch> parseBranches(InputStream is) {
        var typeRef = new TypeReference<List<BranchesEndpoint.Branch>>() {};
        try {
            return objMapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }

    public static CodeSearchEndpoint.SearchResult parseTextMatches(InputStream is) {
        var typeRef = new TypeReference<CodeSearchEndpoint.SearchResult>() {};
        try {
            return objMapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }
}
