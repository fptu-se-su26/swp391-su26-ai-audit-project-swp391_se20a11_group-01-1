const Loading = ({ fullscreen = false }) => {
  if (fullscreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-white/70 z-50">
        <div className="w-12 h-12 border-4 border-orange-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }
  return (
    <div className="flex items-center justify-center py-16">
      <div className="w-10 h-10 border-4 border-orange-500 border-t-transparent rounded-full animate-spin" />
    </div>
  );
};

export default Loading;
