package com.example.triply.data.repository;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.ChatMessage;
import com.example.triply.data.remote.model.ChatThread;
import com.example.triply.data.remote.model.CreateThreadRequest;
import com.example.triply.data.remote.model.SendMessageRequest;
import com.example.triply.data.remote.model.SendMessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private final ApiService apiService;

    public interface CreateThreadCallback {
        void onSuccess(ChatThread thread);
        void onError(String message);
    }

    public interface GetThreadsCallback {
        void onSuccess(List<ChatThread> threads);
        void onError(String message);
    }

    public interface GetMessagesCallback {
        void onSuccess(List<ChatMessage> messages);
        void onError(String message);
    }

    public interface SendMessageCallback {
        void onSuccess(SendMessageResponse response);
        void onError(String message);
    }

    public ChatRepository() {
        this.apiService = RetrofitClient.api();
    }

    public void createThread(String title, CreateThreadCallback callback) {
        apiService.createThread(new CreateThreadRequest(title)).enqueue(new Callback<ChatThread>() {
            @Override
            public void onResponse(Call<ChatThread> call, Response<ChatThread> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Tạo thread thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatThread> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getThreads(GetThreadsCallback callback) {
        apiService.getThreads().enqueue(new Callback<List<ChatThread>>() {
            @Override
            public void onResponse(Call<List<ChatThread>> call, Response<List<ChatThread>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lấy danh sách thread thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatThread>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getMessages(int threadId, GetMessagesCallback callback) {
        apiService.getMessages(threadId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lấy lịch sử chat thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void sendMessage(int threadId, String message, SendMessageCallback callback) {
        apiService.sendMessage(new SendMessageRequest(threadId, message)).enqueue(new Callback<SendMessageResponse>() {
            @Override
            public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Gửi tin nhắn thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}

