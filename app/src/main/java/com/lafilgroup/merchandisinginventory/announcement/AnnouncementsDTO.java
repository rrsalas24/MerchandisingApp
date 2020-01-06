package com.lafilgroup.merchandisinginventory.announcement;

import java.util.ArrayList;

/**
 * Created by RR SALAS on 4/3/2018.
 */

public class AnnouncementsDTO
{
    public ArrayList<AnnouncementDTO> items;

    public ArrayList<AnnouncementDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<AnnouncementDTO> items) {
        this.items = items;
    }

    public class AnnouncementDTO
    {
        public String created_at;
        public String first_name;
        public String last_name;
        public String message;
        public String image_path;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }
    }
}
