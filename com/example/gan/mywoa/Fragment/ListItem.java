package com.example.gan.mywoa.Fragment;

public class ListItem {

	private String headline;
	private String tipe;
	private String status;
	private String intstatus;
	private String detail;
	private String kode;
	private String paketId;
	private String harga;
	private String tglreq;
	private double nominalharga;
	private String url;

	private String paketMaxHours ="1";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getTipe() {
		return tipe;
	}

	public void setTipe(String tipe) {
		this.tipe = tipe;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "" + detail;
	}

	public String getHarga() {
		return harga;
	}

	public void setHarga(String harga) {
		this.harga = harga;
	}
	public void setPaketId(String paketId) {
		this.paketId = paketId;
	}
	public String getPaketId() {
		return paketId;
	}
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public double getNominalharga() {
		return nominalharga;
	}

	public void setNominalharga(double nominalharga) {
		this.nominalharga = nominalharga;
	}

	public String getIntstatus() {
		return intstatus;
	}

	public void setIntstatus(String intstatus) {
		this.intstatus = intstatus;
	}

	public String getPaketMaxHours() {
		return paketMaxHours;
	}

	public void setPaketMaxHours(String paketMaxHours) {
		this.paketMaxHours = paketMaxHours;
	}

	public String getKode() {
		return kode;
	}

	public void setKode(String kode) {
		this.kode = kode;
	}

	public String getTglreq() {
		return tglreq;
	}

	public void setTglreq(String tglreq) {
		this.tglreq = tglreq;
	}
}
