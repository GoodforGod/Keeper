package com.keeper.service.modelbased.impl;

/*
 * Created by @GoodforGod on 08.05.2017.
 */

import com.keeper.model.dao.Picture;
import com.keeper.model.dto.PictureDTO;
import com.keeper.repo.PictureRepository;
import com.keeper.service.modelbased.IPictureService;
import com.keeper.util.ModelTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.keeper.util.resolvers.ErrorMessageResolver.*;

/**
 * Default Comment
 */
@Service
public class PictureService extends PrimeModelService<Picture, Long> implements IPictureService {

    private final PictureRepository repository;

    @Autowired
    public PictureService(PictureRepository repository) {
        this.repository = repository;
        setup(repository);
    }

    @Override
    public Optional<Picture> getByUserId(Long userId) {
        if(invalidId(userId, REMOVE_NULLABLE_ID))
            return Optional.empty();

        return repository.findByUserId(userId);
    }

    @Override
    public Optional<Picture> getByTaskId(Long taskId) {
        if(invalidId(taskId, REMOVE_NULLABLE_ID + "TASK"))
            return Optional.empty();

        return repository.findByTaskId(taskId);
    }

    @Transactional
    @Override
    public Optional<Picture> saveDTO(PictureDTO model) {
        if(model == null) {
            logger.warn(CREATE_MODEL_NULLABLE);
            return Optional.empty();
        }

        return super.save(ModelTranslator.toDAO(model));
    }

    @Transactional
    @Override
    public Optional<Picture> updateDTO(PictureDTO model) {
        if(model == null) {
            logger.warn(UPDATE_MODEL_NULLABLE);
            return Optional.empty();
        }

        Optional<Picture> toSave = get(model.getId());

        if(!toSave.isPresent()) {
            logger.warn(UPDATE_NOT_FOUND);
            return Optional.empty();
        }

        return super.save(ModelTranslator.updateDAO(toSave.get(), model));
    }

    @Transactional
    @Override
    public Optional<Picture> removeByUserId(Long userId) {
        if(invalidId(userId, REMOVE_NULLABLE_ID + "USER"))
            return Optional.empty();

        return Optional.empty();
    }

    @Transactional
    @Override
    public Optional<Picture> removeByTaskId(Long taskId) {
        if(invalidId(taskId, REMOVE_NULLABLE_ID + "TASK"))
            return Optional.empty();

        return Optional.empty();
    }
}
