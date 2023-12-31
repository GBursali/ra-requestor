package space.gbsdev.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.http.Cookie;
import io.restassured.specification.RequestSpecification;
import space.gbsdev.utils.InvalidJSONException;
import space.gbsdev.utils.JSONUtils;

import java.io.File;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;

/**
 * Base class for building REST API endpoints. It provides methods for configuring the base URL,
 * creating endpoints with specific paths and JSON data, and handling request specifications.
 */
@SuppressWarnings("java:S1144")
public class EndpointBase {

    private RequestSpecification requestSpecification;
    private Path jsonBasePath;

    /**
     * Constructs an EndpointBase with the specified base URL.
     *
     * @param baseURL The base URL for the REST API.
     */
    protected EndpointBase(String baseURL) {
        this.requestSpecification = given().baseUri(baseURL);
    }

    /**
     * Creates a new EndpointBase with the specified base URL.
     *
     * @param baseURL The base URL for the REST API.
     * @return The created EndpointBase instance.
     */
    public static EndpointBase builder(String baseURL) {
        return new EndpointBase(baseURL);
    }

    /**
     * Creates a new Endpoint with the specified path relative to the base URL.
     *
     * @param path The path to append to the base URL.
     * @return The created Endpoint instance.
     */
    public Endpoint makeWithPath(String path) {
        return Endpoint.fromBase(this).withPath(path);
    }

    /**
     * Adds a cookie to the request specification.
     *
     * @param cookie The cookie to add.
     * @return The updated EndpointBase instance.
     */
    public EndpointBase withCookie(Cookie cookie) {
        this.requestSpecification = requestSpecification.cookie(cookie);
        return this;
    }

    /**
     * Sets the base path for JSON files used in endpoint configurations.
     *
     * @param jsonBasePath The base path for JSON files.
     * @return The updated EndpointBase instance.
     */
    public EndpointBase withJsonBasePath(Path jsonBasePath) {
        this.jsonBasePath = jsonBasePath;
        return this;
    }

    /**
     * Gets the raw request specification associated with this endpoint base.
     *
     * @return The raw request specification.
     */
    public RequestSpecification getRawRequest() {
        return this.requestSpecification;
    }

    /**
     * Creates a new Endpoint with the JSON data from the specified file.
     *
     * @param jsonPath The path to the JSON file.
     * @return The created Endpoint instance.
     * @throws InvalidJSONException If the JSON file is not found or is malformed.
     */
    public Endpoint makeWithJson(Path jsonPath) {
        if (jsonBasePath != null)
            jsonPath = jsonBasePath.resolve(jsonPath);

        File jsonFile = jsonPath.toFile();
        if (!jsonFile.exists())
            throw new InvalidJSONException("JSON file not found", jsonPath);


        JsonObject contents = JSONUtils.readJsonFile(jsonPath,"Malformed JSON file supplied");
        return Endpoint.fromJson(this, contents);
    }

    /**
     * Creates a new Endpoint with the JSON data from the specified file.
     *
     * @param jsonPath The path to the JSON file.
     * @return The created Endpoint instance.
     * @throws InvalidJSONException If the JSON file is not found or is malformed.
     */
    public Endpoint makeWithJson(String jsonPath) {
        return makeWithJson(Path.of(jsonPath));
    }

    /**
     * Parses the given content and returns a JsonElement.
     *
     * @param content The JSON content to parse.
     * @return The parsed JsonElement.
     */
    protected JsonElement jsonify(String content) {
        return JsonParser.parseString(content);
    }
}
