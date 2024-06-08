# timedlist component

do you have objects which need to be expired? i do. for instance, one request/response driven messaging application requires that responses arriving late are marked invalid. the timedlist component is employed to expire any response which arrives later than the duration specified by the caller.

please look at the dynamic tests to see if the accuracy is sufficient.
