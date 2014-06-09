package net.sourcewalker.android.calculon;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.android.apps.common.testing.ui.espresso.IdlingResource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class VolleyIdlingResource implements IdlingResource {

    private final RequestQueue queue;
    private final Field currentRequestsField;
    private ResourceCallback resourceCallback;
    private boolean idle = true;

    public VolleyIdlingResource(RequestQueue queue) {
        this.queue = queue;
        this.currentRequestsField = setupField(queue);
    }

    private Field setupField(RequestQueue queue) {
        try {
            Field field = RequestQueue.class.getDeclaredField("mCurrentRequests");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            // Just throw a runtime exception. This is testing after all...
            throw new RuntimeException("Field not found!", e);
        }
    }

    @Override
    public String getName() {
        return "VolleyRequests";
    }

    @Override
    public boolean isIdleNow() {
        try {
            Set<Request> requests = (Set<Request>) currentRequestsField.get(queue);
            boolean newIdle = requests == null || requests.size() == 0;
            if (newIdle != idle) {
                idle = newIdle;
                if (idle && resourceCallback != null) {
                    resourceCallback.onTransitionToIdle();
                }
            }
            return newIdle;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can not access field!", e);
        }
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

}
