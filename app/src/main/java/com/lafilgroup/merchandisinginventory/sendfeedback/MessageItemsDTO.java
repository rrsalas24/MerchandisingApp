package com.lafilgroup.merchandisinginventory.sendfeedback;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 6/4/2018.
 */

public class MessageItemsDTO
{
    public ArrayList<MessageItemDTO> items;

    public ArrayList<MessageItemDTO> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<MessageItemDTO> items)
    {
        this.items = items;
    }

    public class MessageItemDTO
    {
        public String message_id;
        public String merchandiser_id;
        public String message;
        public String created_at;
        public String last_name;
        public String first_name;

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

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }
    }
}
