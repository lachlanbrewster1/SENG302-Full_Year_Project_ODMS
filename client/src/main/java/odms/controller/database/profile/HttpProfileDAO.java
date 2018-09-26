package odms.controller.database.profile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import odms.Session;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;
import odms.data.NHIConflictException;


@Slf4j
public class HttpProfileDAO implements ProfileDAO {

    @Override
    public List<Profile> getAll() {
        String url = "http://localhost:6969/api/v1/profiles/all";
        Map<String, Object> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    @Override
    public Profile get(int profileId) {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(profileId));
        return getSingleRequest(url, queryParams);
    }

    @Override
    public Profile get(String username) {
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        return getSingleRequest(url, queryParams);
    }

    @Override
    public void add(Profile profile) throws NHIConflictException, SQLException {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/profiles";
        Map<String, Object> queryParams = new HashMap<>();
        Response response = null;

        String body = gson.toJson(profile);
        Request request = new Request(url, queryParams, body);
        try {
            response = request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (response != null) {
            if (response.getStatus() == 400) {
                throw new NHIConflictException("NHI in use.", profile.getNhi());
            } else if (response.getStatus() == 500) {
                throw new SQLException(response.getBody());
            }
        }
    }

    @Override
    public boolean isUniqueUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int isUniqueNHI(String nhi) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Profile profile) {
        String url = "http://localhost:6969/api/v1/profiles/" + profile.getId();
        Request request = new Request(url, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(Profile profile) {
        Gson gson = new Gson();
        String url = "http://localhost:6969/api/v1/profiles/" + profile.getId();
        String body = gson.toJson(profile);
        Request request = new Request(url, new HashMap<>(), body);
        try {
            request.patch();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Profile> search(String searchString, int ageSearchInt, int ageRangeSearchInt,
            String region, String gender, String type, Set<OrganEnum> organs) {
        String url = "http://localhost:6969/api/v1/profiles/all";

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("searchString", searchString);
        queryParams.put("ageSearchInt", ageSearchInt);
        queryParams.put("ageRangeSearchInt", ageRangeSearchInt);
        queryParams.put("region", region);
        queryParams.put("gender", gender);
        queryParams.put("type", type);
        queryParams.put("organs", organs);

        return getArrayRequest(url, queryParams);
    }

    @Override
    public Integer size() {
        JsonParser parser = new JsonParser();
        String url = "http://localhost:6969/api/v1/profiles/count";
        Map<String, Object> queryParams = new HashMap<>();
        Request request = new Request(url, queryParams);
        Response response = null;
        int count = 0;

        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null) {
            count = parser.parse(response.getBody()).getAsInt();
        }
        return count;
    }

    @Override
    public List<Entry<Profile, OrganEnum>> getAllReceiving() {
        String url = "http://localhost:6969/api/v1/profiles/receivers";
        Map<String, Object> queryParams = new HashMap<>();
        return getEntryArrayRequest(url, queryParams);
    }

    @Override
    public List<Entry<Profile, OrganEnum>> searchReceiving(String searchString) {
        String url = "http://localhost:6969/api/v1/profiles/receivers";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("searchString", searchString);
        return getEntryArrayRequest(url, queryParams);
    }

    @Override
    public List<Profile> getOrganReceivers(String organ, String bloodType, Integer lowerAgeRange,
            Integer upperAgeRange) {
        String url = "http://localhost:6969/api/v1/profiles/receivers";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("organ", organ);
        queryParams.put("bloodType", bloodType.replace("+", "%2B"));
        queryParams.put("lowerAgeRange", lowerAgeRange);
        queryParams.put("upperAgeRange", upperAgeRange);

        return getArrayRequest(url, queryParams);
    }

    @Override
    public List<Profile> getDead() throws SQLException {
        String url = "http://localhost:6969/api/v1/profiles/dead";
        Map<String, Object> queryParams = new HashMap<>();
        return getArrayRequest(url, queryParams);
    }

    private Profile getSingleRequest(String url, Map<String, Object> queryParams) {
        Gson parser = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null && response.getStatus() == 200) {
            return parser.fromJson(response.getBody(), Profile.class);
        }
        return null;
    }

    private List<Profile> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        List<Profile> profiles = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                profiles.add(gson.fromJson(result, Profile.class));
            }
        }
        return profiles;
    }

    private List<Entry<Profile, OrganEnum>> getEntryArrayRequest(String url,
            Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();

        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        List<Entry<Profile, OrganEnum>> profiles = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                Profile profile = gson.fromJson(result.getAsJsonObject().get("key"), Profile.class);
                OrganEnum organ = gson
                        .fromJson(result.getAsJsonObject().get("value"), OrganEnum.class);
                profiles.add(new SimpleEntry<>(profile, organ));
            }
        }
        return profiles;
    }

    @Override
    public Boolean hasPassword(String nhi) {
        Response response = null;
        String url = "http://localhost:6969/api/v1/setup/password";

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("nhi", nhi);
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null) {
            return response.getBody().equals("true");
        }
        return null;
    }

    @Override
    public Boolean checkCredentials(String username, String password) {
        JsonParser parser = new JsonParser();
        String url = "http://localhost:6969/api/v1/login";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        queryParams.put("password", password);
        queryParams.put("UserType", UserType.PROFILE);

        Request request = new Request(url, queryParams, "{}");
        Response response = null;
        try {
            response = request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null) {
            if (response.getStatus() == 200) {
                JsonObject body = parser.parse(response.getBody()).getAsJsonObject();

                Profile profile = new Profile(username);
                profile.setId(body.get("id").getAsInt());

                Session.setCurrentUser(profile, UserType.PROFILE);
                Session.setToken(body.get("Token").getAsInt());
                return true;
            }
            if (response.getStatus() == 400) {
                throw new IllegalArgumentException("Invalid details.");
            }
        }
        return false;
    }

    @Override
    public Boolean savePassword(String username, String password) {
        String url = "http://localhost:6969/api/v1/setup/password";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", username);
        queryParams.put("password", password);

        Request request = new Request(url, queryParams, "{}");
        Response response = null;
        try {
            response = request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null) {
            if (response.getStatus() == 200) {
                return true;
            }
            if (response.getStatus() == 400) {
                return false;
            }
        }
        return false;
    }
}
