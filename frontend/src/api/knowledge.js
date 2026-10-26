import { request } from "./client";

export const knowledgeApi = {
  login(credentials) {
    return request("/api/login", { method: "POST", body: credentials });
  },
  listDocuments(token) {
    return request("/api/documents", { token });
  },
  createDocument(token, document) {
    return request("/api/documents", { method: "POST", token, body: document });
  },
  updateDocument(token, documentId, document) {
    return request(`/api/documents/${documentId}`, { method: "PUT", token, body: document });
  },
  ask(token, question) {
    return request("/api/questions", { method: "POST", token, body: { question } });
  }
};
