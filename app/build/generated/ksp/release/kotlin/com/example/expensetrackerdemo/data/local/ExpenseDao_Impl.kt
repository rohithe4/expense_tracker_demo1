package com.example.expensetrackerdemo.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.expensetrackerdemo.`data`.model.Template
import com.example.expensetrackerdemo.`data`.model.Transaction
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ExpenseDao_Impl(
  __db: RoomDatabase,
) : ExpenseDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTemplate: EntityInsertAdapter<Template>

  private val __insertAdapterOfTransaction: EntityInsertAdapter<Transaction>

  private val __deleteAdapterOfTransaction: EntityDeleteOrUpdateAdapter<Transaction>

  private val __updateAdapterOfTransaction: EntityDeleteOrUpdateAdapter<Transaction>
  init {
    this.__db = __db
    this.__insertAdapterOfTemplate = object : EntityInsertAdapter<Template>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `templates` (`id`,`name`,`category`,`sampleText`,`type`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Template) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.sampleText)
        statement.bindLong(5, entity.type.toLong())
      }
    }
    this.__insertAdapterOfTransaction = object : EntityInsertAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `transactions` (`id`,`templateId`,`name`,`amount`,`type`,`category`,`source`,`date`,`note`,`reference`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
        val _tmpTemplateId: Int? = entity.templateId
        if (_tmpTemplateId == null) {
          statement.bindNull(2)
        } else {
          statement.bindLong(2, _tmpTemplateId.toLong())
        }
        statement.bindText(3, entity.name)
        statement.bindDouble(4, entity.amount)
        statement.bindLong(5, entity.type.toLong())
        statement.bindText(6, entity.category)
        statement.bindText(7, entity.source)
        statement.bindLong(8, entity.date)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNote)
        }
        val _tmpReference: String? = entity.reference
        if (_tmpReference == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpReference)
        }
        statement.bindLong(11, entity.createdAt)
      }
    }
    this.__deleteAdapterOfTransaction = object : EntityDeleteOrUpdateAdapter<Transaction>() {
      protected override fun createQuery(): String = "DELETE FROM `transactions` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfTransaction = object : EntityDeleteOrUpdateAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `transactions` SET `id` = ?,`templateId` = ?,`name` = ?,`amount` = ?,`type` = ?,`category` = ?,`source` = ?,`date` = ?,`note` = ?,`reference` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
        val _tmpTemplateId: Int? = entity.templateId
        if (_tmpTemplateId == null) {
          statement.bindNull(2)
        } else {
          statement.bindLong(2, _tmpTemplateId.toLong())
        }
        statement.bindText(3, entity.name)
        statement.bindDouble(4, entity.amount)
        statement.bindLong(5, entity.type.toLong())
        statement.bindText(6, entity.category)
        statement.bindText(7, entity.source)
        statement.bindLong(8, entity.date)
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNote)
        }
        val _tmpReference: String? = entity.reference
        if (_tmpReference == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpReference)
        }
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertTemplate(template: Template): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfTemplate.insertAndReturnId(_connection, template)
    _result
  }

  public override suspend fun insertTransaction(transaction: Transaction): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfTransaction.insertAndReturnId(_connection, transaction)
    _result
  }

  public override suspend fun deleteTransaction(transaction: Transaction): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfTransaction.handle(_connection, transaction)
  }

  public override suspend fun updateTransaction(transaction: Transaction): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfTransaction.handle(_connection, transaction)
  }

  public override fun getAllTemplates(): Flow<List<Template>> {
    val _sql: String = "SELECT * FROM templates"
    return createFlow(__db, false, arrayOf("templates")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfSampleText: Int = getColumnIndexOrThrow(_stmt, "sampleText")
        val _cursorIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _result: MutableList<Template> = mutableListOf()
        while (_stmt.step()) {
          val _item: Template
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          val _tmpSampleText: String
          _tmpSampleText = _stmt.getText(_cursorIndexOfSampleText)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_cursorIndexOfType).toInt()
          _item = Template(_tmpId,_tmpName,_tmpCategory,_tmpSampleText,_tmpType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllTransactions(): Flow<List<Transaction>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY date DESC, id DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTemplateId: Int = getColumnIndexOrThrow(_stmt, "templateId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpTemplateId: Int?
          if (_stmt.isNull(_cursorIndexOfTemplateId)) {
            _tmpTemplateId = null
          } else {
            _tmpTemplateId = _stmt.getLong(_cursorIndexOfTemplateId).toInt()
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_cursorIndexOfType).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_cursorIndexOfSource)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpNote: String?
          if (_stmt.isNull(_cursorIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_cursorIndexOfNote)
          }
          val _tmpReference: String?
          if (_stmt.isNull(_cursorIndexOfReference)) {
            _tmpReference = null
          } else {
            _tmpReference = _stmt.getText(_cursorIndexOfReference)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              Transaction(_tmpId,_tmpTemplateId,_tmpName,_tmpAmount,_tmpType,_tmpCategory,_tmpSource,_tmpDate,_tmpNote,_tmpReference,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY date DESC, id DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTemplateId: Int = getColumnIndexOrThrow(_stmt, "templateId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpTemplateId: Int?
          if (_stmt.isNull(_cursorIndexOfTemplateId)) {
            _tmpTemplateId = null
          } else {
            _tmpTemplateId = _stmt.getLong(_cursorIndexOfTemplateId).toInt()
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_cursorIndexOfType).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_cursorIndexOfSource)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpNote: String?
          if (_stmt.isNull(_cursorIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_cursorIndexOfNote)
          }
          val _tmpReference: String?
          if (_stmt.isNull(_cursorIndexOfReference)) {
            _tmpReference = null
          } else {
            _tmpReference = _stmt.getText(_cursorIndexOfReference)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              Transaction(_tmpId,_tmpTemplateId,_tmpName,_tmpAmount,_tmpType,_tmpCategory,_tmpSource,_tmpDate,_tmpNote,_tmpReference,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTopTransactions(): Flow<List<Transaction>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY date DESC, id DESC LIMIT 5"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTemplateId: Int = getColumnIndexOrThrow(_stmt, "templateId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpTemplateId: Int?
          if (_stmt.isNull(_cursorIndexOfTemplateId)) {
            _tmpTemplateId = null
          } else {
            _tmpTemplateId = _stmt.getLong(_cursorIndexOfTemplateId).toInt()
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_cursorIndexOfType).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_cursorIndexOfSource)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpNote: String?
          if (_stmt.isNull(_cursorIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_cursorIndexOfNote)
          }
          val _tmpReference: String?
          if (_stmt.isNull(_cursorIndexOfReference)) {
            _tmpReference = null
          } else {
            _tmpReference = _stmt.getText(_cursorIndexOfReference)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              Transaction(_tmpId,_tmpTemplateId,_tmpName,_tmpAmount,_tmpType,_tmpCategory,_tmpSource,_tmpDate,_tmpNote,_tmpReference,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTransactionById(id: Int): Transaction? {
    val _sql: String = "SELECT * FROM transactions WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTemplateId: Int = getColumnIndexOrThrow(_stmt, "templateId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: Transaction?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpTemplateId: Int?
          if (_stmt.isNull(_cursorIndexOfTemplateId)) {
            _tmpTemplateId = null
          } else {
            _tmpTemplateId = _stmt.getLong(_cursorIndexOfTemplateId).toInt()
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpType: Int
          _tmpType = _stmt.getLong(_cursorIndexOfType).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_cursorIndexOfSource)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpNote: String?
          if (_stmt.isNull(_cursorIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_cursorIndexOfNote)
          }
          val _tmpReference: String?
          if (_stmt.isNull(_cursorIndexOfReference)) {
            _tmpReference = null
          } else {
            _tmpReference = _stmt.getText(_cursorIndexOfReference)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _result =
              Transaction(_tmpId,_tmpTemplateId,_tmpName,_tmpAmount,_tmpType,_tmpCategory,_tmpSource,_tmpDate,_tmpNote,_tmpReference,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTotalIncome(): Flow<Double?> {
    val _sql: String = "SELECT SUM(amount) FROM transactions WHERE type = 1"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTotalExpense(): Flow<Double?> {
    val _sql: String = "SELECT SUM(amount) FROM transactions WHERE type = -1"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
