package rasterdata;

import annotations.NotNull;

public interface Presentable<DeviceType> {
	@NotNull DeviceType present(@NotNull DeviceType device);
}
