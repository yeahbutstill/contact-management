create table event_publication (
        id uuid not null,
        completion_date timestamp(6) with time zone,
        event_type varchar(255),
        listener_id varchar(255),
        publication_date timestamp(6) with time zone,
        serialized_event varchar(255),
        primary key (id)
);
