package com.apaza.moises.notevoice.database;

import com.apaza.moises.notevoice.database.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "IMAGE".
 */
public class Image {

    private Long id;
    private String code;
    private String route;
    private String description;
    private java.util.Date createAt;
    private Long idNote;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ImageDao myDao;

    private Note note;
    private Long note__resolvedKey;


    public Image() {
    }

    public Image(Long id) {
        this.id = id;
    }

    public Image(Long id, String code, String route, String description, java.util.Date createAt, Long idNote) {
        this.id = id;
        this.code = code;
        this.route = route;
        this.description = description;
        this.createAt = createAt;
        this.idNote = idNote;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getImageDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(java.util.Date createAt) {
        this.createAt = createAt;
    }

    public Long getIdNote() {
        return idNote;
    }

    public void setIdNote(Long idNote) {
        this.idNote = idNote;
    }

    /** To-one relationship, resolved on first access. */
    public Note getNote() {
        Long __key = this.idNote;
        if (note__resolvedKey == null || !note__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteDao targetDao = daoSession.getNoteDao();
            Note noteNew = targetDao.load(__key);
            synchronized (this) {
                note = noteNew;
            	note__resolvedKey = __key;
            }
        }
        return note;
    }

    public void setNote(Note note) {
        synchronized (this) {
            this.note = note;
            idNote = note == null ? null : note.getId();
            note__resolvedKey = idNote;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
