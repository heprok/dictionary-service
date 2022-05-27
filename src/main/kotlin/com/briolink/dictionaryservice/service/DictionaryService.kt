package com.briolink.dictionaryservice.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DictionaryService() {
    companion object : KLogging()
}
