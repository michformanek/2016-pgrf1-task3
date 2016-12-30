package geometry;

import java.util.List;

import annotations.NotNull;

public interface Solid<VertexType, ConnectivityType> {
	class Part<ConnectivityType> {
		private final int startIndex, primitiveCount;
		private final @NotNull ConnectivityType connectivityType;
		public Part(final int startIndex, final int primitiveCount, 
				final @NotNull ConnectivityType connectivityType) {
			this.startIndex = startIndex;
			this.primitiveCount = primitiveCount;
			this.connectivityType = connectivityType;
		}
		public int getStartIndex() {
			return startIndex;
		}
		public int getPrimitiveCount() {
			return primitiveCount;
		}
		public @NotNull ConnectivityType getConnectivityType() {
			return connectivityType;
		}
	}
	@NotNull List<VertexType> getVertices();
	@NotNull List<Integer> getIndices();
	@NotNull List<Part<ConnectivityType>> getParts();
}
