package com.lafilgroup.merchandisinginventory.sendfeedback;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/1/2018.
 */

public class MessagesDTO
{
    public ArrayList<MessageDTO> items;

    public ArrayList<MessageDTO> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<MessageDTO> items) {
        this.items = items;
    }

    public class MessageDTO
    {
        public String message_id;
        public String merchandiser_id;
        public String send_to;
        public String subject;
        public String message;
        public String seen_by_sender;
        public String seen_by_receiver;
        public String created_at;

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }

        public String getMerchandiser_id() {
            return merchandiser_id;
        }

        public void setMerchandiser_id(String merchandiser_id) {
            this.merchandiser_id = merchandiser_id;
        }

        public String getSend_to() {
            return send_to;
        }

        public void setSend_to(String send_to) {
            this.send_to = send_to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getSeen_by_sender() {
            return seen_by_sender;
        }

        public void setSeen_by_sender(String seen_by_sender) {
            this.seen_by_sender = seen_by_sender;
        }

        public String getSeen_by_receiver() {
            return seen_by_receiver;
        }

        public void setSeen_by_receiver(String seen_by_receiver) {
            this.seen_by_receiver = seen_by_receiver;
        }
    }
}
