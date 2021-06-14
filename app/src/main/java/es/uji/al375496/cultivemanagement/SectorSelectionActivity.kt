package es.uji.al375496.cultivemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import es.uji.al375496.cultivemanagement.model.SectorSelectionModel
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import es.uji.al375496.cultivemanagement.presenter.SectorSelectionPresenter
import es.uji.al375496.cultivemanagement.view.SectorSelectionView

class SectorSelectionActivity : AppCompatActivity(), SectorSelectionView
{
    override var fixSectorName: Boolean = false
    override var fixSubsectorName: Boolean = false

    private lateinit var sectorTextView: TextView
    private lateinit var sectorAutoCompleteTextView: AutoCompleteTextView
    private lateinit var sectorButton: Button
    private lateinit var subsectorTextView: TextView
    private lateinit var subsectorAutoCompleteTextView: AutoCompleteTextView
    private lateinit var subsectorButton: Button
    private lateinit var allButton: Button

    private var sectorTextWatcher: TextWatcher? = null
    private var subsectorTextWatcher: TextWatcher? = null

    private lateinit var loadingTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    private var shouldShowSubsector = false
    private var recoveredSectorString: String? = null

    private var recoveredSubsectorString: String? = null

    private lateinit var presenter: SectorSelectionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sector_selection)

        sectorTextView = findViewById(R.id.sectorTextView)
        sectorAutoCompleteTextView = findViewById(R.id.sectorAutoCompleteTextView)
        sectorButton = findViewById(R.id.sectorGoButton)
        subsectorTextView = findViewById(R.id.subsectorTextView)
        subsectorAutoCompleteTextView = findViewById(R.id.subsectorAutoCompleteTextView)
        subsectorButton = findViewById(R.id.subsectorGoButton)
        allButton = findViewById(R.id.allGoButton)

        loadingTextView = findViewById(R.id.loadingTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        lateinit var model: SectorSelectionModel
        if (savedInstanceState != null){
            model = savedInstanceState.getParcelable(MODEL)!!
            recoveredSectorString = savedInstanceState.getString(SECTOR)
            recoveredSubsectorString = savedInstanceState.getString(SUBSECTOR)
        }
        else
            model = SectorSelectionModel()

        presenter = SectorSelectionPresenter(this, model, this)
    }

    override var enabledSectorButton: Boolean
        get() = sectorButton.isEnabled
        set(value) {sectorButton.isEnabled = value}

    override var enabledSubsectorButton: Boolean
        get() = subsectorButton.isEnabled
        set(value) {subsectorButton.isEnabled = value}

    override var visibleSubsector: Boolean
        get() = shouldShowSubsector
        set(value) {
            shouldShowSubsector = value
            if (value) {
                subsectorButton.visibility = View.VISIBLE
                subsectorAutoCompleteTextView.visibility = View.VISIBLE
                subsectorTextView.visibility = View.VISIBLE
            } else {
                subsectorButton.visibility = View.GONE
                subsectorAutoCompleteTextView.visibility = View.GONE
                subsectorTextView.visibility = View.GONE
            }
        }

    override var loading: Boolean
        get() = loadingProgressBar.visibility == View.VISIBLE
        set(value) {
            if (value){
                loadingTextView.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.VISIBLE

                sectorTextView.visibility = View.INVISIBLE
                sectorAutoCompleteTextView.visibility = View.INVISIBLE
                sectorButton.visibility = View.INVISIBLE
                allButton.visibility = View.INVISIBLE

                subsectorButton.visibility = View.GONE
                subsectorAutoCompleteTextView.visibility = View.GONE
                subsectorTextView.visibility = View.GONE
            } else {
                loadingTextView.visibility = View.INVISIBLE
                loadingProgressBar.visibility = View.INVISIBLE

                sectorTextView.visibility = View.VISIBLE
                sectorAutoCompleteTextView.visibility = View.VISIBLE
                sectorButton.visibility = View.VISIBLE
                allButton.visibility = View.VISIBLE

                visibleSubsector = shouldShowSubsector
            }
        }

    override fun onGoButton(view: View) {
        when (view.id)
        {
            R.id.allGoButton -> presenter.onAllButton()
            R.id.sectorGoButton -> presenter.onSectorButton()
            R.id.subsectorGoButton -> presenter.onSubsectorButton()
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.setup(recoveredSectorString, recoveredSubsectorString)

        recoveredSectorString = null
        recoveredSubsectorString = null
    }

    override fun populateSectors(sectors: List<Sector>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sectors)
        sectorAutoCompleteTextView.apply{
            setAdapter(adapter)
            if (sectorTextWatcher != null)
                removeTextChangedListener(sectorTextWatcher)
            sectorTextWatcher = object: TextWatcher
            {
                override fun afterTextChanged(s: Editable?) {
                    val id = s.toString().substringBefore(":").toIntOrNull() ?: s.toString().toIntOrNull()
                    if (id != null){
                        sectors.binarySearch { it.id.compareTo(id) }.let {
                            if(it >= 0){
                                val aux = fixSectorName
                                fixSectorName = false

                                if (s.toString() == sectors[it].toString())
                                    presenter.onSectorSelected(sectors[it])
                                else if (aux)
                                    sectorAutoCompleteTextView.setText(sectors[it].toString())
                            }
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    presenter.onSectorDeselected()
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
            addTextChangedListener(sectorTextWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isEmpty())
                    showDropDown()
            }
        }
    }

    override fun populateSubsectors(subsectors: List<Subsector>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subsectors)
        subsectorAutoCompleteTextView.apply{
            setAdapter(adapter)
            if (subsectorTextWatcher != null)
                removeTextChangedListener(subsectorTextWatcher)
            subsectorTextWatcher = object: TextWatcher
            {
                override fun afterTextChanged(s: Editable?) {
                    val id = s.toString().substringBefore(":").toIntOrNull() ?: s.toString().toIntOrNull()
                    if (id != null){
                        subsectors.binarySearch { it.id.compareTo(id) }.let {
                            if(it >= 0){
                                val aux = fixSubsectorName
                                fixSubsectorName = false

                                if (s.toString() == subsectors[it].toString())
                                    presenter.onSubsectorSelected(subsectors[it])
                                else if (aux)
                                    subsectorAutoCompleteTextView.setText(subsectors[it].toString())
                            }
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    presenter.onSubsectorDeselected()
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
            addTextChangedListener(subsectorTextWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isEmpty())
                    showDropDown()
            }
        }
    }

    override fun launchShowNotesActivity(sector: Sector?, subsector: Subsector?) {
        recoveredSectorString = sectorAutoCompleteTextView.text.toString()
        recoveredSubsectorString = subsectorAutoCompleteTextView.text.toString()

        val intent = Intent(this, ShowNotesActivity::class.java).apply {
            putExtra(SECTOR, sector)
            putExtra(SUBSECTOR, subsector)
        }
        startActivity(intent)
    }

    override fun setSectorText(s: String) {
        sectorAutoCompleteTextView.setText(s)
    }

    override fun setSubsectorText(s: String) {
        subsectorAutoCompleteTextView.setText(s)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(MODEL, presenter.model)
        outState.putString(SECTOR, sectorAutoCompleteTextView.text.toString())
        outState.putString(SUBSECTOR, subsectorAutoCompleteTextView.text.toString())
        super.onSaveInstanceState(outState)
    }
}