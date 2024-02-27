package okhttp;

import com.google.gson.Gson;
import dto.ContactDTO;
import dto.MessageDTO;
import dto.ErrorDTO;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

public class DeleteContactByIDOkhttp {

    String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoibGl6YTE5QGdtYWlsLmNvbSIsImlzcyI6IlJlZ3VsYWl0IiwiZXhwIjoxNzA5NDY3NTMwLCJpYXQiOjE3MDg4Njc1MzB9.BxiaIJ7Ce923tt0luBXgvCLFWeO_vPbdPGMUq5NoB8I";

    Gson gson= new Gson();

    OkHttpClient client = new OkHttpClient();
    String id;

    public static final MediaType JSON= MediaType.get("application/json;charset=utf-8");

    @BeforeMethod
    public void preCondition() throws IOException {
        int i = new Random().nextInt(1000)+1000;
        System.out.println(i);
        ContactDTO contactDTO = ContactDTO.builder()
                .name("Katya")
                .lastName("Kim")
                .email("katya"+i+"gmail.com")
                .phone("76576475"+i)
                .address("street")
                .description("friend")
                .build();
        RequestBody body = RequestBody.create(gson.toJson(contactDTO),JSON);
        Request request=new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts")
                .post(body)
                .addHeader("Authorization",token)
                .build();
        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());
        MessageDTO messageDTO = gson.fromJson(response.body().string(), MessageDTO.class);
        String message = messageDTO.getMessage();
        String[] all = message.split(": ");
        id=all[1];



    }

    @Test
    public void deleteContactByIdSuccess() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts/"+id)
                .delete()
                .addHeader("Authorization",token)
                .build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(response.code(),200);
        MessageDTO dto = gson.fromJson(response.body().string(), MessageDTO.class);
        System.out.println(dto.getMessage());
        Assert.assertEquals(dto.getMessage(),"Contact was deleted!");



    }

    @Test
    public void deleteContactByIdWrongToken() throws IOException {
        Request request = new Request.Builder()
                .url("https://contactapp-telran-backend.herokuapp.com/v1/contacts/63d24c34-7833-0f8621")
                .delete()
                .addHeader("Authorization","hghgjgh")
                .build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(response.code(),401);
        ErrorDTO errorDTO = gson.fromJson(response.body().string(), ErrorDTO.class);
        Assert.assertEquals(errorDTO.getError(), "Unauthorized");


    }
}
