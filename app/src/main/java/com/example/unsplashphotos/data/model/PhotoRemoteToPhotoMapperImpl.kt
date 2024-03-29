package com.example.unsplashphotos.data.model

import com.example.unsplashphotos.domain.model.Photo
import com.example.unsplashphotos.utils.PhotoRemoteToPhotoMapper
import javax.inject.Inject

class PhotoRemoteToPhotoMapperImpl @Inject constructor() :
    PhotoRemoteToPhotoMapper<PhotoRemoteEntity, Photo> {

    override fun mapFromEntity(entity: PhotoRemoteEntity): Photo {
        return Photo(
            id = entity.id ?: "",
            altDescription = entity.altDescription ?: "",
            urls = mapToDomainUrls(entity.urlsRemote),
            links = mapToDomainLinks(entity.linksRemote),
            likes = entity.likes ?: 0,
        )
    }

    private fun mapToDomainUrls(urlsRemote: UrlsRemote?): Photo.Urls {
        return Photo.Urls(
            urlsRemote?.regular ?: "",
            urlsRemote?.thumb ?: "",
        )
    }

    private fun mapToDomainLinks(linksRemote: LinksRemote?): Photo.Links {
        return Photo.Links(
            linksRemote?.download ?: "",
            linksRemote?.downloadLocation ?: "",
            linksRemote?.html ?: "",
            linksRemote?.self ?: "",
        )
    }

    override fun mapToEntity(domainModel: Photo): PhotoRemoteEntity {
        return PhotoRemoteEntity(
            id = domainModel.id,
            altDescription = domainModel.altDescription,
            urlsRemote = UrlsRemote(),
            linksRemote = LinksRemote(),
            likes = domainModel.likes,
        )
    }
}
