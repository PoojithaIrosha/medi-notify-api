package com.poojithairosha.medinotifyapi.controller;

import static com.poojithairosha.medinotifyapi.controller.ApiUrls.CommonUrls.API;
import static com.poojithairosha.medinotifyapi.controller.ApiUrls.CommonUrls.BY_ID;

public interface ApiUrls {

    interface CommonUrls {
        String API = "/api";
        String BY_ID = "/{id}";
    }

    interface PatientsUrls {
        String PATIENTS = API + "/patients";
        String PATIENTS_BY_ID = PATIENTS + BY_ID;
    }

    interface PractitionersUrls {
        String PRACTITIONERS = API + "/practitioners";
        String PRACTITIONERS_BY_ID = PRACTITIONERS + BY_ID;
    }

    interface AppointmentsUrl {
        String APPOINTMENTS = API + "/appointments";
        String APPOINTMENTS_BY_ID = APPOINTMENTS + BY_ID;
        String APPOINTMENTS_CANCEL_BY_ID = APPOINTMENTS + BY_ID + "/cancel";
    }

}
