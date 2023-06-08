package ru.mirea.tsybulko.mieraproject.ui.compass

import java.io.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TSAGeoMag {
    private val input = arrayOf(
        "   2020.0            WMM-2020        12/10/2019",
        "  1  0  -29404.5       0.0        6.7        0.0",
        "  1  1   -1450.7    4652.9        7.7      -25.1",
        "  2  0   -2500.0       0.0      -11.5        0.0",
        "  2  1    2982.0   -2991.6       -7.1      -30.2",
        "  2  2    1676.8    -734.8       -2.2      -23.9",
        "  3  0    1363.9       0.0        2.8        0.0",
        "  3  1   -2381.0     -82.2       -6.2        5.7",
        "  3  2    1236.2     241.8        3.4       -1.0",
        "  3  3     525.7    -542.9      -12.2        1.1",
        "  4  0     903.1       0.0       -1.1        0.0",
        "  4  1     809.4     282.0       -1.6        0.2",
        "  4  2      86.2    -158.4       -6.0        6.9",
        "  4  3    -309.4     199.8        5.4        3.7",
        "  4  4      47.9    -350.1       -5.5       -5.6",
        "  5  0    -234.4       0.0       -0.3        0.0",
        "  5  1     363.1      47.7        0.6        0.1",
        "  5  2     187.8     208.4       -0.7        2.5",
        "  5  3    -140.7    -121.3        0.1       -0.9",
        "  5  4    -151.2      32.2        1.2        3.0",
        "  5  5      13.7      99.1        1.0        0.5",
        "  6  0      65.9       0.0       -0.6        0.0",
        "  6  1      65.6     -19.1       -0.4        0.1",
        "  6  2      73.0      25.0        0.5       -1.8",
        "  6  3    -121.5      52.7        1.4       -1.4",
        "  6  4     -36.2     -64.4       -1.4        0.9",
        "  6  5      13.5       9.0       -0.0        0.1",
        "  6  6     -64.7      68.1        0.8        1.0",
        "  7  0      80.6       0.0       -0.1        0.0",
        "  7  1     -76.8     -51.4       -0.3        0.5",
        "  7  2      -8.3     -16.8       -0.1        0.6",
        "  7  3      56.5       2.3        0.7       -0.7",
        "  7  4      15.8      23.5        0.2       -0.2",
        "  7  5       6.4      -2.2       -0.5       -1.2",
        "  7  6      -7.2     -27.2       -0.8        0.2",
        "  7  7       9.8      -1.9        1.0        0.3",
        "  8  0      23.6       0.0       -0.1        0.0",
        "  8  1       9.8       8.4        0.1       -0.3",
        "  8  2     -17.5     -15.3       -0.1        0.7",
        "  8  3      -0.4      12.8        0.5       -0.2",
        "  8  4     -21.1     -11.8       -0.1        0.5",
        "  8  5      15.3      14.9        0.4       -0.3",
        "  8  6      13.7       3.6        0.5       -0.5",
        "  8  7     -16.5      -6.9        0.0        0.4",
        "  8  8      -0.3       2.8        0.4        0.1",
        "  9  0       5.0       0.0       -0.1        0.0",
        "  9  1       8.2     -23.3       -0.2       -0.3",
        "  9  2       2.9      11.1       -0.0        0.2",
        "  9  3      -1.4       9.8        0.4       -0.4",
        "  9  4      -1.1      -5.1       -0.3        0.4",
        "  9  5     -13.3      -6.2       -0.0        0.1",
        "  9  6       1.1       7.8        0.3       -0.0",
        "  9  7       8.9       0.4       -0.0       -0.2",
        "  9  8      -9.3      -1.5       -0.0        0.5",
        "  9  9     -11.9       9.7       -0.4        0.2",
        " 10  0      -1.9       0.0        0.0        0.0",
        " 10  1      -6.2       3.4       -0.0       -0.0",
        " 10  2      -0.1      -0.2       -0.0        0.1",
        " 10  3       1.7       3.5        0.2       -0.3",
        " 10  4      -0.9       4.8       -0.1        0.1",
        " 10  5       0.6      -8.6       -0.2       -0.2",
        " 10  6      -0.9      -0.1       -0.0        0.1",
        " 10  7       1.9      -4.2       -0.1       -0.0",
        " 10  8       1.4      -3.4       -0.2       -0.1",
        " 10  9      -2.4      -0.1       -0.1        0.2",
        " 10 10      -3.9      -8.8       -0.0       -0.0",
        " 11  0       3.0       0.0       -0.0        0.0",
        " 11  1      -1.4      -0.0       -0.1       -0.0",
        " 11  2      -2.5       2.6       -0.0        0.1",
        " 11  3       2.4      -0.5        0.0        0.0",
        " 11  4      -0.9      -0.4       -0.0        0.2",
        " 11  5       0.3       0.6       -0.1       -0.0",
        " 11  6      -0.7      -0.2        0.0        0.0",
        " 11  7      -0.1      -1.7       -0.0        0.1",
        " 11  8       1.4      -1.6       -0.1       -0.0",
        " 11  9      -0.6      -3.0       -0.1       -0.1",
        " 11 10       0.2      -2.0       -0.1        0.0",
        " 11 11       3.1      -2.6       -0.1       -0.0",
        " 12  0      -2.0       0.0        0.0        0.0",
        " 12  1      -0.1      -1.2       -0.0       -0.0",
        " 12  2       0.5       0.5       -0.0        0.0",
        " 12  3       1.3       1.3        0.0       -0.1",
        " 12  4      -1.2      -1.8       -0.0        0.1",
        " 12  5       0.7       0.1       -0.0       -0.0",
        " 12  6       0.3       0.7        0.0        0.0",
        " 12  7       0.5      -0.1       -0.0       -0.0",
        " 12  8      -0.2       0.6        0.0        0.1",
        " 12  9      -0.5       0.2       -0.0       -0.0",
        " 12 10       0.1      -0.9       -0.0       -0.0",
        " 12 11      -1.1      -0.0       -0.0        0.0",
        " 12 12      -0.3       0.5       -0.1       -0.1"
    )

    private var alt = 0.0
    private var glat = 0.0
    private var glon = 0.0
    private var time = 0.0
    private var dec = 0.0
    private var dip = 0.0
    private var ti = 0.0
    private val maxdeg = 12
    private var maxord = 0
    private var defaultDate = 2022.5
    private val defaultAltitude = 0.0
    private val c = Array(13) { DoubleArray(13) }
    private val cd = Array(13) { DoubleArray(13) }
    private val tc = Array(13) {
        DoubleArray(
            13
        )
    }
    private val dp = Array(13) { DoubleArray(13 ) }

    private val snorm = DoubleArray(169)
    private val sp = DoubleArray(13)
    private val cp = DoubleArray(13)
    private val fn = DoubleArray(13)
    private val fm = DoubleArray(13)
    private val pp = DoubleArray(13)
    private val k = Array(13) { DoubleArray(13) }

    private var otime = 0.0
    private var oalt = 0.0
    private var olat = 0.0
    private var olon = 0.0

    private var epoch = 0.0

    private var bx = 0.0
    private var by = 0.0
    private var bz = 0.0
    private var bh = 0.0

    private var re = 0.0
    private var a2 = 0.0
    private var b2 = 0.0
    private var c2 = 0.0
    private var a4 = 0.0
    private var b4 = 0.0
    private var c4 = 0.0
    private var r = 0.0
    private var d = 0.0
    private var ca = 0.0
    private var sa = 0.0
    private var ct = 0.0
    private var st
            = 0.0

    init {
        initModel()
    }

    private fun initModel() {
        glat = 0.0
        glon = 0.0

        maxord = maxdeg
        sp[0] = 0.0
        pp[0] = 1.0
        snorm[0] = pp[0]
        cp[0] = snorm[0]
        dp[0][0] = 0.0
        val a = 6378.137
        val b = 6356.7523142
        re = 6371.2
        a2 = a * a
        b2 = b * b
        c2 = a2 - b2
        a4 = a2 * a2
        b4 = b2 * b2
        c4 = a4 - b4
        try {
            val `is`: Reader
            val input = javaClass.getResourceAsStream("WMM.COF")
                ?: throw FileNotFoundException("WMM.COF not found")
            `is` = InputStreamReader(input)
            val str = StreamTokenizer(`is`)



            c[0][0] = 0.0
            cd[0][0] = 0.0
            str.nextToken()
            epoch = str.nval
            defaultDate = epoch + 2.5
            str.nextToken()
            str.nextToken()
            while (true) {
                str.nextToken()
                if (str.nval >= 9999) // end of file
                    break
                val n = str.nval.toInt()
                str.nextToken()
                val m = str.nval.toInt()
                str.nextToken()
                val gnm = str.nval
                str.nextToken()
                val hnm = str.nval
                str.nextToken()
                val dgnm = str.nval
                str.nextToken()
                val dhnm = str.nval
                if (m <= n) {
                    c[m][n] = gnm
                    cd[m][n] = dgnm
                    if (m != 0) {
                        c[n][m - 1] = hnm
                        cd[n][m - 1] = dhnm
                    }
                }
            }
            `is`.close()
        }

        catch (e: FileNotFoundException) {
            val msg = """
                
                NOTICE      NOTICE      NOTICE      
                WMMCOF file not found in TSAGeoMag.InitModel()
                The input file WMM.COF was not found in the same
                directory as the application.
                The magnetic field components are set to internal values.
                
                """.trimIndent()
            setCoeff()
        } catch (e: IOException) {
            val msg = """
       
                NOTICE      NOTICE      NOTICE      
                Problem reading the WMMCOF file in TSAGeoMag.InitModel()
                The input file WMM.COF was found, but there was a problem 
                reading the data.
                The magnetic field components are set to internal values.
                """.trimIndent()
            setCoeff()
        }

        snorm[0] = 1.0
        for (n in 1..maxord) {
            snorm[n] = snorm[n - 1] * (2 * n - 1) / n
            var j = 2
            var m = 0
            val D1 = 1
            var D2 = (n - m + D1) / D1
            while (D2 > 0) {
                k[m][n] =
                    ((n - 1) * (n - 1) - m * m).toDouble() / ((2 * n - 1) * (2 * n - 3)).toDouble()
                if (m > 0) {
                    val flnmj = (n - m + 1) * j / (n + m).toDouble()
                    snorm[n + m * 13] = snorm[n + (m - 1) * 13] * Math.sqrt(flnmj)
                    j = 1
                    c[n][m - 1] = snorm[n + m * 13] * c[n][m - 1]
                    cd[n][m - 1] = snorm[n + m * 13] * cd[n][m - 1]
                }
                c[m][n] = snorm[n + m * 13] * c[m][n]
                cd[m][n] = snorm[n + m * 13] * cd[m][n]
                D2--
                m += D1
            }
            fn[n] = (n + 1).toDouble()
            fm[n] = n.toDouble()
        } //for(n...)
        k[1][1] = 0.0
        olon = -1000.0
        olat = olon
        oalt = olat
        otime = oalt
    }

    private fun calcGeoMag(fLat: Double, fLon: Double, year: Double, altitude: Double) {
        glat = fLat
        glon = fLon
        alt = altitude
        time = year
        val dt = time - epoch
        val pi = Math.PI
        val dtr = pi / 180.0
        val rlon = glon * dtr
        val rlat = glat * dtr
        val srlon = sin(rlon)
        val srlat = sin(rlat)
        val crlon = cos(rlon)
        val crlat = cos(rlat)
        val srlat2 = srlat * srlat
        val crlat2 = crlat * crlat
        sp[1] = srlon
        cp[1] = crlon

        if (alt != oalt || glat != olat) {
            val q = sqrt(a2 - c2 * srlat2)
            val q1 = alt * q
            val q2 = (q1 + a2) / (q1 + b2) * ((q1 + a2) / (q1 + b2))
            ct = srlat / sqrt(q2 * crlat2 + srlat2)
            st = sqrt(1.0 - ct * ct)
            val r2 = alt * alt + 2.0 * q1 + (a4 - c4 * srlat2) / (q * q)
            r = sqrt(r2)
            d = sqrt(a2 * crlat2 + b2 * srlat2)
            ca = (alt + d) / r
            sa = c2 * crlat * srlat / (r * d)
        }
        if (glon != olon) {
            for (m in 2..maxord) {
                sp[m] = sp[1] * cp[m - 1] + cp[1] * sp[m - 1]
                cp[m] = cp[1] * cp[m - 1] - sp[1] * sp[m - 1]
            }
        }
        val aor = re / r
        var ar = aor * aor
        var br = 0.0
        var bt = 0.0
        var bp = 0.0
        var bpp = 0.0
        for (n in 1..maxord) {
            ar *= aor
            var m = 0
            val D3 = 1
            var D4 = (n + m + D3) / D3
            while (D4 > 0) {
                if (alt != oalt || glat != olat) {
                    if (n == m) {
                        snorm[n + m * 13] = st * snorm[n - 1 + (m - 1) * 13]
                        dp[m][n] = st * dp[m - 1][n - 1] + ct * snorm[n - 1 + (m - 1) * 13]
                    }
                    if (n == 1 && m == 0) {
                        snorm[n + m * 13] = ct * snorm[n - 1 + m * 13]
                        dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13]
                    }
                    if (n > 1 && n != m) {
                        if (m > n - 2) snorm[n - 2 + m * 13] = 0.0
                        if (m > n - 2) dp[m][n - 2] = 0.0
                        snorm[n + m * 13] =
                            ct * snorm[n - 1 + m * 13] - k[m][n] * snorm[n - 2 + m * 13]
                        dp[m][n] =
                            ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13] - k[m][n] * dp[m][n - 2]
                    }
                }

                if (time != otime) {
                    tc[m][n] = c[m][n] + dt * cd[m][n]
                    if (m != 0) tc[n][m - 1] = c[n][m - 1] + dt * cd[n][m - 1]
                }


                var temp1: Double
                var temp2: Double
                val par = ar * snorm[n + m * 13]
                if (m == 0) {
                    temp1 = tc[m][n] * cp[m]
                    temp2 = tc[m][n] * sp[m]
                } else {
                    temp1 = tc[m][n] * cp[m] + tc[n][m - 1] * sp[m]
                    temp2 = tc[m][n] * sp[m] - tc[n][m - 1] * cp[m]
                }
                bt -= ar * temp1 * dp[m][n]
                bp += fm[m] * temp2 * par
                br += fn[n] * temp1 * par


                if (st == 0.0 && m == 1) {
                    if (n == 1) pp[n] = pp[n - 1] else pp[n] = ct * pp[n - 1] - k[m][n] * pp[n - 2]
                    val parp = ar * pp[n]
                    bpp += fm[m] * temp2 * parp
                }
                D4--
                m += D3
            }
        }
        if (st == 0.0) bp = bpp else bp /= st


        bx = -bt * ca - br * sa
        by = bp
        bz = bt * sa - br * ca


        bh = sqrt(bx * bx + by * by)
        ti = sqrt(bh * bh + bz * bz)

        dec = atan2(by, bx) / dtr

        dip = atan2(bz, bh) / dtr
        otime = time
        oalt = alt
        olat = glat
        olon = glon
    }

    fun getDeclination(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return dec
    }

    fun getDeclination(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return dec
    }

    fun getIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return ti
    }

    fun getIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return ti
    }

    fun getHorizontalIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bh
    }

    fun getHorizontalIntensity(
        dlat: Double,
        dlong: Double,
        year: Double,
        altitude: Double
    ): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bh
    }

    fun getVerticalIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bz
    }

    fun getVerticalIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bz
    }

    fun getNorthIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return bx
    }

    fun getNorthIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return bx
    }

    fun getEastIntensity(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return by
    }

    fun getEastIntensity(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return by
    }

    fun getDipAngle(dlat: Double, dlong: Double): Double {
        calcGeoMag(dlat, dlong, defaultDate, defaultAltitude)
        return dip
    }

    fun getDipAngle(dlat: Double, dlong: Double, year: Double, altitude: Double): Double {
        calcGeoMag(dlat, dlong, year, altitude)
        return dip
    }

    private fun setCoeff() {
        c[0][0] = 0.0
        cd[0][0] = 0.0
        epoch = input[0].trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toDouble()
        defaultDate = epoch + 2.5
        var tokens: Array<String>

        //loop to get data from internal values
        for (i in 1 until input.size) {
            tokens =
                input[i].trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val n = tokens[0].toInt()
            val m = tokens[1].toInt()
            val gnm = tokens[2].toDouble()
            val hnm = tokens[3].toDouble()
            val dgnm = tokens[4].toDouble()
            val dhnm = tokens[5].toDouble()
            if (m <= n) {
                c[m][n] = gnm
                cd[m][n] = dgnm
                if (m != 0) {
                    c[n][m - 1] = hnm
                    cd[n][m - 1] = dhnm
                }
            }
        }
    }

    fun decimalYear(cal: GregorianCalendar): Double {
        val year = cal[Calendar.YEAR]
        val daysInYear: Double
        daysInYear = if (cal.isLeapYear(year)) {
            366.0
        } else {
            365.0
        }
        return year + cal[Calendar.DAY_OF_YEAR] / daysInYear
    }
}